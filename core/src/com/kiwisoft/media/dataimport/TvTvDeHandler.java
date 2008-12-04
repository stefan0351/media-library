package com.kiwisoft.media.dataimport;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.xml.*;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.progress.ProgressSupport;

/**
 * @author Stefan Stiller
*/
abstract class TvTvDeHandler<T>
{
	private T object;
	private TVTVDeLoader loader;
	private ProgressSupport progressSupport;

	private Pattern dayPattern=Pattern.compile("<td id=\"date-box\"  class=\"fb-w13\" >(\\d{1,2}\\. \\w+ \\d{4})</td>");
	private Pattern timePattern=Pattern.compile("<td class=\"pitime-box\\d\" nowrap=\"nowrap\" style='width:80px;'>(\\d\\d:\\d\\d)</td>");
	private Pattern channelPattern=Pattern.compile("<td class=\"pisicon-box\\d\" nowrap style=\"width:40px\">"+
												   "<img title=\"([^\"]+)\" src=\"(http://www.tvtv.de:80/tvtv/resource\\?channelLogo=(\\d+))\" alt=\"channel logo\"");
	private Pattern titlePattern=Pattern.compile("<td class=\"pititle-box\\d\"");
	private Pattern lengthPattern=Pattern.compile("class=\"fn-w8\" id=\"box-small-light\">L\u00e4nge: (\\d+) min\\.</td>");

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

	private static Date mergeDayAndTime(Date day, Date time, TimeZone timeZone)
	{
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeZone(timeZone);
		calendar.setTime(day);
		Calendar calendar2=Calendar.getInstance();
		calendar2.setTimeZone(timeZone);
		calendar2.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar2.get(Calendar.MILLISECOND));
		return calendar.getTime();
	}

	private Set<TvTvDeAirdateData> loadDates(Set<String> patterns)
	{
		progressSupport.startStep("Load schedule for "+getName()+"...");
		String resultPage;
		try
		{
			Set<TvTvDeAirdateData> airdates=new HashSet<TvTvDeAirdateData>();
			for (String pattern : patterns)
			{
				resultPage=WebUtils.loadURL(MessageFormat.format(TVTVDeLoader.SEARCH_URL, pattern), null, "UTF-8");

				SimpleDateFormat dayFormat=new SimpleDateFormat("d. MMMM yyyy", Locale.GERMANY);
				TimeZone timeZone=TimeZone.getTimeZone("Europe/Berlin");
				dayFormat.setTimeZone(timeZone);
				SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
				timeFormat.setTimeZone(timeZone);
				Matcher dayMatcher=dayPattern.matcher(resultPage);

				int dayPosition=0;
				boolean dayFound=dayMatcher.find(dayPosition);
				while (dayFound)
				{
					String dayString=dayMatcher.group(1);
					Date day=dayFormat.parse(dayString);
					int dayStart=dayMatcher.end();
					dayFound=dayMatcher.find(dayStart);
					int dayEnd=dayFound ? dayMatcher.start() : resultPage.length();

					String daySubPage=resultPage.substring(dayStart, dayEnd);
					Matcher timeMatcher=timePattern.matcher(daySubPage);
					boolean timeFound=timeMatcher.find();
					while (timeFound)
					{
						TvTvDeAirdateData airdate=new TvTvDeAirdateData();
						String timeString=timeMatcher.group(1);

						airdate.setTime(mergeDayAndTime(day, timeFormat.parse(timeString), timeZone));
						int timeStart=timeMatcher.end();
						timeFound=timeMatcher.find(timeMatcher.end(0));
						int timeEnd=timeFound ? timeMatcher.start() : daySubPage.length();
						String timeSubPage=daySubPage.substring(timeStart, timeEnd);

						Matcher channelMatcher=channelPattern.matcher(timeSubPage);
						if (channelMatcher.find())
						{
							airdate.setChannelName(channelMatcher.group(1));
							airdate.setChannelLogo(channelMatcher.group(2));
							airdate.setChannelKey(channelMatcher.group(3));
						}
						Matcher titleMatcher=titlePattern.matcher(timeSubPage);
						if (titleMatcher.find())
						{
							int titleStart=timeSubPage.indexOf(">", titleMatcher.end());
							int titleEnd=timeSubPage.indexOf("</td>", titleStart);
							String title=timeSubPage.substring(titleStart, titleEnd);
							XMLUtils.Tag linkTag=XMLUtils.getNextTag(title, 0, "a");
							if (linkTag!=null)
							{
								String href=XMLUtils.getAttribute(linkTag.text, "href");
								if (!StringUtils.isEmpty(href)) airdate.setDetailLink(TVTVDeLoader.BASE_URL+href);
							}
							airdate.setTitle(XMLUtils.removeTags(title).trim());
						}
						airdates.add(airdate);
					}
				}

			}
			for (Iterator it=airdates.iterator(); it.hasNext();)
			{
				if (progressSupport.isStoppedByUser()) return null;
				TvTvDeAirdateData airdate=(TvTvDeAirdateData)it.next();
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
				if (airdate.getChannel()==null)
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
						if (!StringUtils.isEmpty(episodeData.getOriginalTitle())) episode1=loader.findEpisode(airdate.getShow(), episodeData.getOriginalTitle());
						Episode episode2=null;
						if (!StringUtils.isEmpty(episodeData.getGermanTitle())) episode2=loader.findEpisode(airdate.getShow(), episodeData.getGermanTitle());

						if (episode1==null) episodeData.setEpisode(episode2);
						else if (episode2==null) episodeData.setEpisode(episode1);
						else if (episode1==episode2) episodeData.setEpisode(episode1);
					}
					if (episodeData.getEpisode()==null)
					{
						episodeData.setEpisode(loader.findEpisode(airdate.getShow(), airdate.getTitle()));
						if (episodeData.getEpisode()!=null)
						{
							episodeData.setGermanTitle(null);
							episodeData.setOriginalTitle(null);
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
				detailPage=WebUtils.loadURL(airdate.getDetailLink(), null, "UTF-8");
				int episodeStart=detailPage.indexOf("<span class=\"fb-b9\">");
				if (episodeStart>0)
				{
					episodeStart=detailPage.indexOf(">", episodeStart)+1;
					int episodeEnd=detailPage.indexOf("</span>", episodeStart);
					String title=detailPage.substring(episodeStart, episodeEnd).trim();
					airdate.setSubTitle(StringUtils.trimQuotes(title));
				}
				Matcher lengthMatcher=lengthPattern.matcher(detailPage);
				if (lengthMatcher.find())
				{
					airdate.setLength(Integer.parseInt(lengthMatcher.group(1)));
				}
				int castStart=detailPage.indexOf("class=\"fn-w8\" id=\"box-small\">Darsteller:</td>");
				if (castStart>0)
				{
					castStart=detailPage.indexOf("<span class=\"fn-b8\">", castStart);
					int castEnd=detailPage.indexOf("</span>", castStart);
					if (castStart>0 && castEnd>castStart)
					{
						airdate.setCast(detailPage.substring(castStart+20, castEnd));
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("content = "+detailPage);
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

		public void handleError(Throwable throwable, boolean rollback)
		{
			progressSupport.error(throwable);
		}
	}
}
