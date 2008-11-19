package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.xml.XMLUtils;

public class TVTVDeLoader implements Job
{
	private static final String BASE_URL="http://www.tvtv.de/tvtv/";
	private static final String SEARCH_URL=BASE_URL
										   +"index.vm?mainTemplate=web/search_result.vm&search_input={0}&x=0&y=0&lang=de"
										   +"&search_psel=display_all&as_rgrp=progStartTime&as_rsort=progStart";

	private List<Object> objects;
	private ProgressSupport progressSupport=new ProgressSupport(this, null);
	private Date now;

	private Pattern dayPattern=Pattern.compile("<td id=\"date-box\"  class=\"fb-w13\" >(\\d{1,2}\\. \\w+ \\d{4})</td>");
	private Pattern timePattern=Pattern.compile("<td class=\"pitime-box\\d\" nowrap=\"nowrap\" style='width:80px;'>(\\d\\d:\\d\\d)</td>");
	private Pattern channelPattern=Pattern.compile("<td class=\"pisicon-box\\d\" nowrap style=\"width:40px\"><img title=\"([^\"]+)\"");
	private Pattern titlePattern=Pattern.compile("<td class=\"pititle-box\\d\"");
	private Pattern lengthPattern=Pattern.compile("class=\"fn-w8\" id=\"box-small-light\">L\u00e4nge: (\\d+) min\\.</td>");

	public TVTVDeLoader(List<Object> objects)
	{
		this.objects=objects;
	}

	public String getName()
	{
		return "Load Schedule from TVTV.de";
	}

	public boolean run(ProgressListener progressListener) throws Exception
	{
		progressSupport=new ProgressSupport(this, progressListener);

		now=new Date();

		List<Object> objects=new ArrayList<Object>();
		if (this.objects==null)
		{
			progressSupport.startStep("Load search patterns...");
			Collection patterns=SearchManager.getInstance().getSearchPattern(SearchPattern.TVTV);

			Iterator it=patterns.iterator();
			while (it.hasNext() && !progressSupport.isStoppedByUser())
			{
				SearchPattern pattern=(SearchPattern)it.next();
				Object reference=pattern.getReference();
				if (reference!=null) objects.add(reference);
				progressSupport.progress(1, true);
			}
		}
		else objects.addAll(this.objects);

		Iterator it=objects.iterator();
		progressSupport.startStep("Load schedules...");
		progressSupport.initialize(true, objects.size(), null);
		while (it.hasNext() && !progressSupport.isStoppedByUser())
		{
			Object object=it.next();
			if (object instanceof Show) new ShowHandler((Show)object).loadSchedule();
			else if (object instanceof Movie) new MovieHandler((Movie)object).loadSchedule();
			else progressSupport.warning("Unhandled object "+object.getClass());
			progressSupport.progress(1, true);
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private Episode findEpisode(Show show, String title)
	{
		Episode episode1=null;
		try
		{
			episode1=ShowManager.getInstance().getEpisodeByName(show, title);
		}
		catch (Exception e)
		{
			progressSupport.error(e);
		}
		return episode1;
	}

	private static String trimQuotes(String episodeTitle)
	{
		if (episodeTitle!=null && episodeTitle.startsWith("\"") && episodeTitle.endsWith("\""))
		{
			return episodeTitle.substring(1, episodeTitle.length()-1).trim();
		}
		return episodeTitle;
	}

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

	private abstract class Handler<T>
	{
		private T object;

		protected Handler(T object)
		{
			this.object=object;
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
				Set<AirdateData> airdates=loadDates(patternStrings);
				if (airdates!=null)
				{
					deleteAirdates();
					createAirdates(airdates);
				}
			}
			else progressSupport.warning("No search pattern defined for "+getName()+".");
		}

		protected abstract Set<SearchPattern> getSearchPatterns();

		protected abstract boolean deleteAirdates();

		protected abstract String getName();

		protected abstract boolean filter(AirdateData airdate);

		private Set<AirdateData> loadDates(Set<String> patterns)
		{
			progressSupport.startStep("Load schedule for "+getName()+"...");
			String resultPage;
			try
			{
				Set<AirdateData> airdates=new HashSet<AirdateData>();
				for (String pattern : patterns)
				{
					resultPage=WebUtils.loadURL(MessageFormat.format(SEARCH_URL, pattern), "UTF-8");

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
							AirdateData airdate=new AirdateData();
							String timeString=timeMatcher.group(1);

							airdate.time=mergeDayAndTime(day, timeFormat.parse(timeString), timeZone);
							int timeStart=timeMatcher.end();
							timeFound=timeMatcher.find(timeMatcher.end(0));
							int timeEnd=timeFound ? timeMatcher.start() : daySubPage.length();
							String timeSubPage=daySubPage.substring(timeStart, timeEnd);

							Matcher channelMatcher=channelPattern.matcher(timeSubPage);
							if (channelMatcher.find())
							{
								airdate.channelName=channelMatcher.group(1);
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
									if (!StringUtils.isEmpty(href)) airdate.detailLink=BASE_URL+href;
								}
								airdate.title=XMLUtils.removeTags(title).trim();
							}
							airdates.add(airdate);
						}
					}

				}
				for (Iterator it=airdates.iterator(); it.hasNext();)
				{
					AirdateData airdate=(AirdateData)it.next();
					airdate.channel=ChannelManager.getInstance().getChannelByName(airdate.channelName);
					airdate.show=ShowManager.getInstance().getShowByName(airdate.title);
					if (airdate.show==null) airdate.movie=MovieManager.getInstance().getMovieByTitle(airdate.title);
					if (airdate.movie!=null) airdate.show=airdate.movie.getShow();
					if (filter(airdate))
					{
						if (airdate.validate()) loadDetails(airdate);
						else it.remove();
					}
					else it.remove();
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

		private void createAirdates(Set<AirdateData> dates)
		{
			int created=0;
			for (final AirdateData airingData : dates)
			{
				MyTransactional transactional=new MyTransactional(airingData);
				if (DBSession.execute(transactional)) created+=transactional.created;
			}
			progressSupport.info(created+" airdates created");
		}

		private Airdate createAirdate(AirdateData airingData)
		{
			Airdate airdate=new Airdate();
			airdate.setChannel(airingData.channel);
			airdate.setDate(airingData.time);
			airdate.setDetailsLink(airingData.detailLink);
			airdate.setShow(airingData.show);
			airdate.setMovie(airingData.movie);
			airdate.setLanguage(airingData.channel.getLanguage());
			airdate.setDataSource(DataSource.TVTV);
			return airdate;
		}

		private List<Airdate> loadDetails(AirdateData airdate)
		{
			if (airdate.show!=null)
			{
				if (!StringUtils.isEmpty(airdate.detailLink))
				{
					String detailPage=null;
					try
					{
						detailPage=WebUtils.loadURL(airdate.detailLink, "UTF-8");
						int episodeStart=detailPage.indexOf("<span class=\"fb-b9\">");
						if (episodeStart>0)
						{
							episodeStart=detailPage.indexOf(">", episodeStart)+1;
							int episodeEnd=detailPage.indexOf("</span>", episodeStart);
							String title=detailPage.substring(episodeStart, episodeEnd).trim();
							title=trimQuotes(title);
							Integer length=null;
							Matcher lengthMatcher=lengthPattern.matcher(detailPage);
							if (lengthMatcher.find())
							{
								length=Integer.parseInt(lengthMatcher.group(1));
							}
							if (title.endsWith(")"))
							{
								int matchingBrace=StringUtils.findMatchingBrace(title, title.length()-1);
								if (matchingBrace>0)
								{
									String originalTitle=title.substring(matchingBrace+1, title.length()-1).trim();
									String germanTitle=title.substring(0, matchingBrace).trim();
									if (length==null || length>3*airdate.show.getDefaultEpisodeLength()/2) // Only check of Double-Episodes if length is 1.5xdefault length
									{
										String[] originalTitles=originalTitle.split("/");
										String[] germanTitles=germanTitle.split("/");
										if (germanTitles.length>1 && germanTitles.length==originalTitles.length)
										{
											int episodeLength=length!=null ? length/2 : airdate.show.getDefaultEpisodeLength();
											for (int i=0; i<germanTitles.length; i++)
											{
												airdate.episodes.add(new EpisodeData(germanTitles[i].trim(), originalTitles[i].trim(), i*episodeLength));
											}
										}
										else airdate.episodes.add(new EpisodeData(germanTitle, originalTitle));
									}
									else airdate.episodes.add(new EpisodeData(germanTitle, originalTitle));
								}
								else airdate.episodes.add(new EpisodeData(title));
							}
							else
							{
								for (String s : title.split("/"))
								{
									airdate.episodes.add(new EpisodeData(s.trim()));
								}
							}
							for (EpisodeData episodeData : airdate.episodes)
							{
								if (episodeData.originalTitle!=null)
								{
									Episode episode1=null;
									if (!StringUtils.isEmpty(episodeData.originalTitle)) episode1=findEpisode(airdate.show, episodeData.originalTitle);
									Episode episode2=null;
									if (!StringUtils.isEmpty(episodeData.germanTitle)) episode2=findEpisode(airdate.show, episodeData.germanTitle);

									if (episode1==null) episodeData.episode=episode2;
									else if (episode2==null) episodeData.episode=episode1;
									else if (episode1==episode2) episodeData.episode=episode1;
								}
								if (episodeData.episode==null)
								{
									episodeData.episode=findEpisode(airdate.show, airdate.title);
									if (episodeData.episode!=null)
									{
										episodeData.germanTitle=null;
										episodeData.originalTitle=null;
									}
								}
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						System.out.println("content = "+detailPage);
						progressSupport.error("Error while loading details for "+airdate.time+" "+airdate.title);
						progressSupport.error(e);
					}
				}
			}
			return null;
		}

		private class MyTransactional implements Transactional
		{
			private final AirdateData airingData;
			public int created;

			public MyTransactional(AirdateData airingData)
			{
				this.airingData=airingData;
			}

			public void run() throws Exception
			{
				if (airingData.episodes.isEmpty())
				{
					createAirdate(airingData);
					created++;
				}
				else
				{
					for (EpisodeData episodeData : airingData.episodes)
					{
						Airdate airdate=createAirdate(airingData);
						if (episodeData.timeOffset>0)
						{
							airdate.setDate(DateUtils.add(airdate.getDate(), Calendar.MINUTE, episodeData.timeOffset));
						}
						Episode episode=episodeData.episode;
						airdate.setEpisode(episode);
						if (episode==null) airdate.setEvent(episodeData.title);
						else if (StringUtils.isEmpty(episode.getGermanTitle()) && !StringUtils.isEmpty(episodeData.germanTitle))
						{
							episode.setGermanTitle(episodeData.germanTitle);
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

	private class ShowHandler extends Handler<Show>
	{
		private ShowHandler(Show object)
		{
			super(object);
		}

		protected String getName()
		{
			return getObject().getTitle();
		}

		protected Set<SearchPattern> getSearchPatterns()
		{
			return SearchManager.getInstance().getSearchPattern(SearchPattern.TVTV, getObject());
		}

		protected boolean deleteAirdates()
		{
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					Connection connection=DBSession.getInstance().getConnection();
					PreparedStatement statement=connection.prepareStatement("delete from airdates where show_id=? and source_id=? and viewdate>=now()");
					try
					{
						statement.setLong(1, getObject().getId());
						statement.setLong(2, DataSource.TVTV.getId());
						int updateCount=statement.executeUpdate();
						progressSupport.info("Deleted "+updateCount+" old airdates for show "+getName());
					}
					finally
					{
						statement.close();
					}
					DBSession.getInstance().getCurrentTransaction().forceCommit();
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					progressSupport.error(throwable);
				}
			});
		}

		protected boolean filter(AirdateData airdate)
		{
			if (airdate.show==getObject()) return true;
			return false;
		}
	}

	private class MovieHandler extends Handler<Movie>
	{
		private MovieHandler(Movie object)
		{
			super(object);
		}

		protected String getName()
		{
			return getObject().getTitle();
		}

		protected Set<SearchPattern> getSearchPatterns()
		{
			return SearchManager.getInstance().getSearchPattern(SearchPattern.TVTV, getObject());
		}

		protected boolean deleteAirdates()
		{
			return DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					Connection connection=DBSession.getInstance().getConnection();
					PreparedStatement statement=connection.prepareStatement("delete from airdates where movie_id=? and source_id=? and viewdate>=now()");
					try
					{
						statement.setLong(1, getObject().getId());
						statement.setLong(2, DataSource.TVTV.getId());
						int updateCount=statement.executeUpdate();
						progressSupport.info("Deleted "+updateCount+" old airdates for movie "+getName());
					}
					finally
					{
						statement.close();
					}
					DBSession.getInstance().getCurrentTransaction().forceCommit();
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					progressSupport.error(throwable);
				}
			});
		}

		protected boolean filter(AirdateData airdate)
		{
			if (airdate.movie==getObject()) return true;
			return false;
		}
	}

	private class AirdateData
	{
		private Date time;
		private String title;
		private String channelName;
		private String detailLink;
		private Show show;
		private Movie movie;
		private Channel channel;
		private List<EpisodeData> episodes=new ArrayList<EpisodeData>();

		public boolean equals(Object o)
		{
			if (this==o) return true;
			if (o==null || getClass()!=o.getClass()) return false;

			AirdateData that=(AirdateData)o;

			if (channelName!=null ? !channelName.equals(that.channelName) : that.channelName!=null) return false;
			if (time!=null ? !time.equals(that.time) : that.time!=null) return false;
			if (title!=null ? !title.equals(that.title) : that.title!=null) return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result=(time!=null ? time.hashCode() : 0);
			result=31*result+(title!=null ? title.hashCode() : 0);
			result=31*result+(channelName!=null ? channelName.hashCode() : 0);
			return result;
		}

		public boolean validate()
		{
			if (time==null)
			{
				progressSupport.error("Airdate without time.");
				return false;
			}
			if (time.before(now)) return false; // Ignore because it is already in the past
			if (channel==null)
			{
				progressSupport.warning("Unknown channel '"+channelName+"'.");
				return false;
			}
			return true;
		}
	}

	private static class EpisodeData
	{
		private int timeOffset;
		private Episode episode;
		private String title;
		private String germanTitle;
		private String originalTitle;

		private EpisodeData(String title)
		{
			this.title=title;
		}

		private EpisodeData(String germanTitle, String originalTitle)
		{
			this(germanTitle, originalTitle, 0);
		}

		private EpisodeData(String germanTitle, String originalTitle, int timeOffset)
		{
			title=germanTitle+" ("+originalTitle+")";
			this.originalTitle=originalTitle;
			this.germanTitle=germanTitle;
			this.timeOffset=timeOffset;
		}
	}
}
