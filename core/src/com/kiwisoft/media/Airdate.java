/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media;

import java.util.Date;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.Identifyable;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.dataImport.DataSource;
import com.kiwisoft.media.dataImport.AirdateData;
import com.kiwisoft.media.movie.Movie;

public class Airdate extends IDObject
{
	public static final String CHANNEL="channel";
	public static final String DATE="date";
	public static final String EPISODE="episode";
	public static final String EVENT="event";
	public static final String SHOW="show";
	public static final String MOVIE="movie";
	public static final String LANGUAGE="language";
	public static final String DATA_SOURCE="dataSource";

	private String event;
	private boolean reminder;
	private Date date;
	private String channelName;

	public Airdate()
	{
	}

	public Airdate(DBDummy dummy)
	{
		super(dummy);
	}

	public Episode getEpisode()
	{
		return (Episode)getReference(EPISODE);
	}

	public void setEpisode(Episode episode)
	{
		setReference(EPISODE, episode);
	}

	public Movie getMovie()
	{
		return (Movie)getReference(MOVIE);
	}

	public void setMovie(Movie movie)
	{
		setReference(MOVIE, movie);
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public String getEvent()
	{
		return event;
	}

	public void setEvent(String event)
	{
		String oldEvent=getEvent();
		this.event=event;
		setModified();
		firePropertyChange(EVENT, oldEvent, event);
	}

	public String getName()
	{
		Movie movie=getMovie();
		if (movie!=null) return movie.getTitle();
		Show show=getShow();
		Episode episode=getEpisode();
		String event=getEvent();
		if (show!=null)
		{
			Language language=getLanguage();
			if (episode!=null)
			{
				if (event!=null) return show.getName(language)+" - "+episode.getNameWithKey(language)+" "+event;
				else  return show.getName(language)+" - "+episode.getNameWithKey(getLanguage());
			}
			else if (event!=null) return show.getName(language)+" - "+event;
			else return show.getName(language);
		}
		return event;
	}

	public boolean isReminder()
	{
		return reminder;
	}

	public void setReminder(boolean reminder)
	{
		this.reminder=reminder;
		setModified();
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		Date oldDate=getDate();
		this.date=date;
		setModified();
		firePropertyChange(DATE, oldDate, date);
	}

	public String getChannelName()
	{
		if (getChannel()!=null) return getChannel().getName();
		return channelName;
	}

	public Channel getChannel()
	{
		return (Channel)getReference(CHANNEL);
	}

	public void setChannel(Channel channel)
	{
		if (channel!=null) channelName=null;
		setReference(CHANNEL, channel);
	}

	public DataSource getDataSource()
	{
		return (DataSource)getReference(DATA_SOURCE);
	}

	public void setDataSource(DataSource dataSource)
	{
		setReference(DATA_SOURCE, dataSource);
	}

	public Identifyable loadReference(String name, Long referenceId)
	{
		if (DATA_SOURCE.equals(name)) return DataSource.get(referenceId);
		return super.loadReference(name, referenceId);
	}

	public boolean equals(Date time, AirdateData airdateData)
	{
		if (getDate().getTime()!=time.getTime()) return false;
		if (airdateData.getShow()!=getShow()) return false;
		if (airdateData.getEpisode()!=null) return airdateData.getEpisode()==getEpisode();
		else if (airdateData.getEvent()!=null) return airdateData.getEvent().equalsIgnoreCase(getEvent());
		return true;
	}

	public static String getName(Show show, Episode episode, String event)
	{
		if (show!=null)
		{
			if (episode!=null) return show+" - "+episode;
			else if (event!=null) return show+" - "+event;
			else return show.toString();
		}
		return event;
	}
}
