package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.xml.XMLUtils;

public class TVTVDeLoader implements Job
{
	private static final String BASE_URL="http://www.tvtv.de/tvtv/";
	private static final String SEARCH_URL=BASE_URL
										   +"index.vm?mainTemplate=web/search_result.vm&search_input={0}&x=0&y=0&lang=de"
										   +"&search_psel=display_all&as_rgrp=progStartTime&as_rsort=progStart";

	private List objects;
	private ProgressSupport progressSupport=new ProgressSupport(this, null);

	public TVTVDeLoader(List objects)
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

		if (objects==null)
		{
			progressSupport.startStep("Load search patterns...");
			Collection patterns=SearchManager.getInstance().getSearchPatterns(SearchPattern.TVTV, Show.class);

			Iterator it=patterns.iterator();
			progressSupport.startStep("Load schedule...");
			progressSupport.initialize(true, patterns.size(), null);
			while (it.hasNext() && !progressSupport.isStoppedByUser())
			{
				SearchPattern pattern=(SearchPattern)it.next();
				Show show=pattern.getShow();
				if (show!=null) loadDates(show.getTitle(), pattern.getPattern());
				progressSupport.progress(1, true);
			}
		}
		else
		{
			Iterator it=objects.iterator();
			progressSupport.initialize(true, objects.size(), null);
			progressSupport.startStep("Load schedule...");
			while (it.hasNext() && !progressSupport.isStoppedByUser())
			{
				Object object=it.next();
				if (object instanceof Show)
				{
					Show show=(Show)object;
					String pattern=show.getSearchPattern(SearchPattern.TVTV);
					if (!StringUtils.isEmpty(pattern))
					{
						loadDates(show.getTitle(), pattern);
					}
					else progressSupport.warning("No pattern for "+show+" found.");
				}
				else if (object instanceof Person)
				{
					Person person=(Person)object;
					String pattern=person.getSearchPattern(SearchPattern.TVTV);
					if (!StringUtils.isEmpty(pattern))
					{
						loadDates(person.getName(), pattern);
					}
					else progressSupport.warning("No pattern for "+person+" found.");
				}
				else progressSupport.warning("Unhandled object class "+object.getClass());
				progressSupport.progress(1, true);
			}
		}
		return true;
	}

	public void dispose() throws IOException
	{
	}

	private List<AirdateData> loadDates(String name, String patternString)
	{
		List<AirdateData> airdates=new ArrayList<AirdateData>();

		progressSupport.startStep("Load schedule for "+name+"...");
		String resultPage=null;
		try
		{
			resultPage=WebUtils.loadURL(MessageFormat.format(SEARCH_URL, patternString), "UTF-8");

			SimpleDateFormat dayFormat=new SimpleDateFormat("d. MMMM yyyy");
			TimeZone timeZone=TimeZone.getTimeZone("Europe/Berlin");
			dayFormat.setTimeZone(timeZone);
			SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
			timeFormat.setTimeZone(timeZone);
			Pattern dayPattern=Pattern.compile("<td id=\"date-box\"  class=\"fb-w13\" >(\\d{1,2}\\. \\w+ \\d{4})</td>");
			Matcher dayMatcher=dayPattern.matcher(resultPage);
			Pattern timePattern=Pattern.compile("<td class=\"pitime-box\\d\" nowrap=\"nowrap\" style='width:80px;'>(\\d\\d:\\d\\d)</td>");
			Pattern channelPattern=Pattern.compile("<td class=\"pisicon-box\\d\" nowrap style=\"width:40px\"><img title=\"([^\"]+)\"");
			Pattern titlePattern=Pattern.compile("<td class=\"pititle-box\\d\"");

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
							airdate.detailLink=XMLUtils.getAttribute(linkTag.text, "href");
						}
						airdate.title=XMLUtils.removeTags(title).trim();
					}
					airdates.add(airdate);
				}
			}

			for (AirdateData airdate : airdates)
			{
				loadDetails(airdate);
				System.out.println("TVTVDeLoader.loadDates: airdate = "+airdate);
			}

			progressSupport.info("Loaded schedule for "+name+".");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("content = "+resultPage);
			progressSupport.error("Loading of schedule for "+name+" failed.");
		}
		return airdates;
	}

	private void loadDetails(AirdateData airdate)
	{
		airdate.channel=ChannelManager.getInstance().getChannelByName(airdate.channelName);
		airdate.show=ShowManager.getInstance().getShowByName(airdate.title);
		if (airdate.show!=null)
		{
			if (!StringUtils.isEmpty(airdate.detailLink))
			{
				String detailPage=null;
				try
				{
					detailPage=WebUtils.loadURL(BASE_URL+airdate.detailLink, "UTF-8");
					int episodeStart=detailPage.indexOf("<span class=\"fb-b9\">");
					if (episodeStart>0)
					{
						episodeStart=detailPage.indexOf(">", episodeStart)+1;
						int episodeEnd=detailPage.indexOf("</span>", episodeStart);
						airdate.episodeTitle=detailPage.substring(episodeStart, episodeEnd).trim();
						airdate.episodeTitle=trimQuotes(airdate.episodeTitle);
						if (airdate.episodeTitle.endsWith(")"))
						{
							int matchingBrace=findMatchingBrace(airdate.episodeTitle, airdate.episodeTitle.length()-1);
							if (matchingBrace>0)
							{
								String originalTitle=airdate.episodeTitle.substring(matchingBrace+1, airdate.episodeTitle.length()-1).trim();
								Episode episode1=findEpisode(airdate.show, originalTitle);
								String germanTitle=airdate.episodeTitle.substring(0, matchingBrace).trim();
								Episode episode2=findEpisode(airdate.show, germanTitle);
								if (episode1==null) airdate.episode=episode2;
								else if (episode2==null) airdate.episode=episode1;
								else if (episode1==episode2) airdate.episode=episode1;
							}
							else airdate.episode=findEpisode(airdate.show, airdate.episodeTitle);
						}
						else airdate.episode=findEpisode(airdate.show, airdate.episodeTitle);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.out.println("content = "+detailPage);
					progressSupport.error("Error while loading details for "+airdate.time+" "+airdate.title);
				}
			}
		}
		else
		{
			airdate.movie=MovieManager.getInstance().getMovieByTitle(airdate.title);
		}
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

	private int findMatchingBrace(String episodeTitle, int index)
	{
		char ch=episodeTitle.charAt(index);
		if (ch==')')
		{
			int level=0;
			int pos=index;
			while (pos>=0)
			{
				ch=episodeTitle.charAt(pos);
				if (ch==')') level--;
				else if (ch=='(') level++;
				if (level==0) return pos;
				pos--;
			}
			return -1;
		}
		else throw new UnsupportedOperationException();
	}

	private String trimQuotes(String episodeTitle)
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

	private static class AirdateData
	{
		private Date time;
		private String title;
		private String channelName;
		private String detailLink;
		private String episodeTitle;
		public Show show;
		public Movie movie;
		public Episode episode;
		public Channel channel;

		public String toString()
		{
			StringBuilder text=new StringBuilder("Airdate(");
			text.append("time=").append(time);
			if (channel!=null) text.append("; channel=").append(channel);
			else text.append("; channelName=").append(channelName);
			if (movie!=null) text.append("; movie=").append(movie);
			else if (show!=null)
			{
				text.append("; show=").append(show);
				if (episode!=null) text.append("; episode=").append(episode);
				else text.append("; episodeTitle=").append(episodeTitle);
			}
			else text.append("; title=").append(title);
			text.append(")");
			return text.toString();
		}
	}
}
