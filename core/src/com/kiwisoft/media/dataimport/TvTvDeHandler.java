package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.*;
import com.kiwisoft.html.HtmlUtils;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
abstract class TvTvDeHandler<T>
{
	private final static Log log=LogFactory.getLog(TvTvDeHandler.class);

	private T object;
	private TVTVDeLoader loader;
	private ProgressSupport progressSupport;

	private Pattern lengthPattern=Pattern.compile("Länge: (\\d+) min\\.");
	private Pattern episodeNumberPattern=Pattern.compile("(\\d+)(?:/\\d+)?.*");

	protected TvTvDeHandler(TVTVDeLoader loader, T object)
	{
		this.loader=loader;
		progressSupport=loader.getProgressSupport();
		this.object=object;
	}

	public ProgressSupport getProgressSupport()
	{
		return progressSupport;
	}

	public T getObject()
	{
		return object;
	}

	protected void loadSchedule()
	{
		Set<String> patternStrings=new HashSet<String>();
		for (SearchPattern pattern : getSearchPatterns())
		{
			if (!StringUtils.isEmpty(pattern.getPattern()))
			{
				patternStrings.add(pattern.getPattern());
			}
		}
		if (!patternStrings.isEmpty())
		{
			Set<TvTvDeAirdateData> airdates=loadDates(patternStrings);
			if (airdates!=null)
			{
				if (!loader.isDryRun())
				{
					deleteAirdates();
					createAirdates(airdates);
				}
			}
		}
		else progressSupport.warning("No search pattern defined for "+getName()+".");
	}

	private void createAirdates(Set<TvTvDeAirdateData> dates)
	{
		int created=0;
		for (final TvTvDeAirdateData airingData : dates)
		{
			SaveTransaction transactional=new SaveTransaction(airingData);
			if (DBSession.execute(transactional)) created+=transactional.created;
		}
		progressSupport.info(created+" airdates created");
	}

	protected abstract Set<SearchPattern> getSearchPatterns();

	protected abstract boolean deleteAirdates();

	protected abstract String getName();

	private Set<TvTvDeAirdateData> loadDates(Set<String> patterns)
	{
		progressSupport.startStep("Load schedule for "+getName()+"...");
		String resultPage;
		try
		{
			Set<TvTvDeAirdateData> airdates=new HashSet<TvTvDeAirdateData>();
			for (String pattern : patterns)
			{
				resultPage=ImportUtils.loadUrl(MessageFormat.format(TVTVDeLoader.SEARCH_URL, pattern), "UTF-8");

				SimpleDateFormat dayFormat=new SimpleDateFormat("EE d. MMMM yyyy", Locale.GERMANY);
				TimeZone timeZone=TimeZone.getTimeZone("Europe/Berlin");
				dayFormat.setTimeZone(timeZone);
				SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
				timeFormat.setTimeZone(timeZone);

				Date maxDate=DateUtils.getStartOfDay(DateUtils.add(new Date(), Calendar.DATE, 10));

				Parser parser=new Parser();
				try
				{
					parser.setInputHTML(resultPage);
					NodeList divTags=parser.parse(new CssSelectorNodeFilter("div.search-list"));
					for (int i=0; i<divTags.size(); i++)
					{
						Div div=(Div) divTags.elementAt(i);
						NodeList list=new NodeList();
						div.collectInto(list, new OrFilter(new CssSelectorNodeFilter("h2"), new CssSelectorNodeFilter("table")));
						Date day=null;
						for (NodeIterator it=list.elements(); it.hasMoreNodes();)
						{
							TagNode node=(TagNode) it.nextNode();
							if ("H2".equalsIgnoreCase(node.getTagName()))
							{
								String dateString=HtmlUtils.trimUnescape(node.toPlainTextString());
								day=dayFormat.parse(dateString);
							}
							else
							{
								if (day.before(maxDate)) airdates.addAll(parseDay(day, (TableTag) node));
							}
						}
					}
				}
				catch (ParserException e)
				{
					progressSupport.error(e);
				}
			}
			for (Iterator it=airdates.iterator(); it.hasNext();)
			{
				if (progressSupport.isStoppedByUser()) return null;
				TvTvDeAirdateData airdate=(TvTvDeAirdateData) it.next();
				if (airdate.getTime()==null)
				{
					progressSupport.error("Airdate without time.");
					it.remove();
					continue;
				}
				if (airdate.getTime().before(loader.getStartDate()))
				{
					// Ignore because it is already in the past
					it.remove();
					continue;
				}
				airdate.setChannel(loader.getChannel(airdate));
				if (airdate.getChannel()==null || !airdate.getChannel().isReceivable())
				{
					it.remove();
					continue;
				}
				if (!preCheck(airdate))
				{
					it.remove();
					continue;
				}
				loadDetails(airdate);
				if (!analyze(airdate)) it.remove();
			}
			progressSupport.info("Found "+airdates.size()+" airdates for "+getName());
			return airdates;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressSupport.error("Loading of schedule for "+getName()+" failed.");
			progressSupport.error(e);
			return null;
		}
	}

	private Set<TvTvDeAirdateData> parseDay(Date day, TableTag tableTag)
	{
		TimeZone timeZone=TimeZone.getTimeZone("Europe/Berlin");
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
		timeFormat.setTimeZone(timeZone);

		TableRow[] rows=tableTag.getRows();
		Set<TvTvDeAirdateData> airdates=new HashSet<TvTvDeAirdateData>();
		for (TableRow row : rows)
		{
			try
			{
				TvTvDeAirdateData airdate=new TvTvDeAirdateData();
				TagNode dateNode=(TagNode) HtmlUtils.findFirst(row, "td.date");
				String timeString=HtmlUtils.trimUnescape(dateNode.toPlainTextString());
				Date time=timeFormat.parse(timeString);
				airdate.setTime(DateUtils.mergeDayAndTime(day, time, timeZone));

				TableColumn channelNode=(TableColumn) HtmlUtils.findFirst(row, "td.channel");
				TagNode channelLogoNode=(TagNode) HtmlUtils.findFirst(channelNode, "img");
				String channel=channelLogoNode.getAttribute("title");
				airdate.setChannelName(channel);
				airdate.setChannelLogo(channelLogoNode.getAttribute("src"));
				Matcher matcher=Pattern.compile("http.*channelLogo=(\\d+)").matcher(airdate.getChannelLogo());
				if (matcher.matches()) airdate.setChannelKey(matcher.group(1));

				TagNode titleNode=(TagNode) HtmlUtils.findFirst(row, "a.title");
				airdate.setTitle(HtmlUtils.trimUnescape(titleNode.toPlainTextString()));
				airdate.setDetailLink(TVTVDeLoader.BASE_URL+titleNode.getAttribute("href"));

				airdates.add(airdate);
			}
			catch (ParseException e)
			{
				progressSupport.error(e);
			}
		}
		return airdates;
	}

	protected boolean preCheck(TvTvDeAirdateData airdate)
	{
		return true;
	}

	protected boolean analyze(TvTvDeAirdateData airdate)
	{
		try
		{
			airdate.setShow(ShowManager.getInstance().getShowByName(airdate.getTitle()));
			if (airdate.getShow()==null) airdate.setMovie(MovieManager.getInstance().getMovieByTitle(airdate.getTitle()));
			if (airdate.getMovie()!=null) airdate.setShow(airdate.getMovie().getShow());
			if (airdate.getShow()!=null)
			{
				Show show=airdate.getShow();
				String title=airdate.getSubTitle();
				if (title.endsWith(")"))
				{
					int matchingBrace=StringUtils.findMatchingBrace(title, title.length()-1);
					if (matchingBrace>0)
					{
						String originalTitle=title.substring(matchingBrace+1, title.length()-1).trim();
						String germanTitle=title.substring(0, matchingBrace).trim();
						Integer length=airdate.getLength();
						if (length==null || length>3*show.getDefaultEpisodeLength()/2) // Only check of Double-Episodes if length is > 1.5xdefault length
						{
							String[] originalTitles=originalTitle.split("/");
							String[] germanTitles=germanTitle.split("/");
							if (germanTitles.length>1 && germanTitles.length==originalTitles.length)
							{
								int episodeLength=length!=null ? length/2 : show.getDefaultEpisodeLength();
								for (int i=0; i<germanTitles.length; i++)
								{
									airdate.addEpisode(new TvTvDeEpisodeData(germanTitles[i].trim(), originalTitles[i].trim(), i*episodeLength));
								}
							}
							else airdate.addEpisode(new TvTvDeEpisodeData(germanTitle, originalTitle));
						}
						else airdate.addEpisode(new TvTvDeEpisodeData(germanTitle, originalTitle));
					}
					else airdate.addEpisode(new TvTvDeEpisodeData(title));
				}
				else
				{
					for (String s : title.split("/"))
					{
						airdate.addEpisode(new TvTvDeEpisodeData(s.trim()));
					}
				}
				for (TvTvDeEpisodeData episodeData : airdate.getEpisodes())
				{
					if (episodeData.getOriginalTitle()!=null)
					{
						Episode episode1=null;
						if (!StringUtils.isEmpty(episodeData.getOriginalTitle()))
							episode1=loader.findEpisode(airdate.getShow(), episodeData.getOriginalTitle());
						Episode episode2=null;
						if (!StringUtils.isEmpty(episodeData.getGermanTitle())) episode2=loader.findEpisode(airdate.getShow(), episodeData.getGermanTitle());

						if (episode1==null) episodeData.setEpisode(episode2);
						else if (episode2==null) episodeData.setEpisode(episode1);
						else if (episode1==episode2) episodeData.setEpisode(episode1);
					}
					if (episodeData.getEpisode()==null)
					{
						if (!StringUtils.isEmpty(episodeData.getTitle()))
						{
							episodeData.setEpisode(loader.findEpisode(airdate.getShow(), episodeData.getTitle()));
							if (episodeData.getEpisode()!=null)
							{
								episodeData.setGermanTitle(null);
								episodeData.setOriginalTitle(null);
							}
						}
						else if (airdate.getEpisodeNumber()!=null)
						{
							episodeData.setTitle("Episode "+airdate.getEpisodeNumber());
						}
					}
				}
			}
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressSupport.error(e);
			return false;
		}
	}

	private void loadDetails(TvTvDeAirdateData airdate)
	{
		if (!StringUtils.isEmpty(airdate.getDetailLink()))
		{
			String detailPage=null;
			try
			{
				detailPage=ImportUtils.loadUrl(airdate.getDetailLink(), "UTF-8");
				Parser parser=new Parser();
				parser.setInputHTML(detailPage);
				CompositeTag table=(CompositeTag) HtmlUtils.findFirst(parser, "table#program-box");
				CompositeTag contentTag=(CompositeTag) HtmlUtils.findFirst(table, "td.program-content");
				CompositeTag titleTag=(CompositeTag) HtmlUtils.findFirst(contentTag, "span.fb-b15");
				if (titleTag!=null)
				{
					String newTitle=HtmlUtils.trimUnescape(titleTag.toPlainTextString());
					if (!StringUtils.isEmpty(newTitle))
					{
						String oldTitle=airdate.getTitle();
						airdate.setTitle(newTitle);
						if (!StringUtils.equal(oldTitle, airdate.getTitle()))
							log.debug("Changed title from '"+oldTitle+"' to '"+airdate.getTitle()+"'.");
					}
				}
				CompositeTag subTitleTag=(CompositeTag) HtmlUtils.findFirst(contentTag, "span.fb-b9");
				if (subTitleTag!=null)
				{
					airdate.setSubTitle(StringUtils.trimQuotes(HtmlUtils.trimUnescape(subTitleTag.toPlainTextString())));
					log.debug("Set subtitle: "+airdate.getSubTitle());
				}
				NodeList numberTags=HtmlUtils.findAll(contentTag, "span.fn-b9");
				if (numberTags.size()>=2)
				{
					CompositeTag numberTag=(CompositeTag) numberTags.elementAt(1);
					String numberString=numberTag.toPlainTextString();
					if (!StringUtils.isEmpty(numberString))
					{
						Matcher matcher=episodeNumberPattern.matcher(numberString);
						if (matcher.matches())
						{
							airdate.setEpisodeNumber(Long.valueOf(matcher.group(1)));
							log.debug("Set episode number: "+airdate.getEpisodeNumber());
						}
						else
						{
							log.warn("Invalid episode number pattern: "+numberString);
							getProgressSupport().warning("Invalid episode number pattern: "+numberString);
						}
					}
				}
				NodeList list=HtmlUtils.findAll(table, "td.fn-w8");
				for (NodeIterator it=list.elements();it.hasMoreNodes();)
				{
					CompositeTag tag=(CompositeTag) it.nextNode();
					String content=tag.toPlainTextString();
					if (content.startsWith("Länge:"))
					{
						Matcher matcher=lengthPattern.matcher(content);
						if (matcher.matches())
						{
							airdate.setLength(Integer.valueOf(matcher.group(1)));
							log.debug("Set length: "+airdate.getLength());
						}
						else
						{
							log.warn("Invalid length pattern: "+content);
							getProgressSupport().warning("Invalid length pattern: "+content);
						}
					}
					else if (content.startsWith("Darsteller:"))
					{
						TableColumn castTag=((TableRow) tag.getParent()).getColumns()[1];
						airdate.setCast(HtmlUtils.trimUnescape(castTag.toPlainTextString()));
						log.debug("Set cast: "+airdate.getCast());
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				progressSupport.error("Error while loading details for "+airdate.getTime()+" "+airdate.getTitle());
				progressSupport.error(e);
			}
		}
	}

	private class SaveTransaction implements Transactional
	{
		private final TvTvDeAirdateData airingData;
		public int created;

		public SaveTransaction(TvTvDeAirdateData airingData)
		{
			this.airingData=airingData;
		}

		@Override
		public void run() throws Exception
		{
			if (airingData.getEpisodes().isEmpty())
			{
				Airdate airdate=loader.createAirdate(airingData, airingData.getTime());
				if (airingData.getShow()==null && airingData.getMovie()==null) airdate.setEvent(airingData.getTitle());
				else airdate.setEvent(airingData.getSubTitle());
				created++;
			}
			else
			{
				for (TvTvDeEpisodeData episodeData : airingData.getEpisodes())
				{
					Date date=DateUtils.add(airingData.getTime(), Calendar.MINUTE, episodeData.getTimeOffset());
					Airdate airdate=loader.createAirdate(airingData, date);
					Episode episode=episodeData.getEpisode();
					airdate.setEpisode(episode);
					if (episode==null) airdate.setEvent(episodeData.getTitle());
					else if (airingData.getChannel()!=null && airingData.getChannel().getLanguage()==LanguageManager.GERMAN)
					{
						if (StringUtils.isEmpty(episode.getGermanTitle()) && !StringUtils.isEmpty(episodeData.getGermanTitle()))
							episode.setGermanTitle(episodeData.getGermanTitle());
					}
					created++;
				}
			}
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			progressSupport.error(throwable);
		}
	}
}
