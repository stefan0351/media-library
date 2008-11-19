/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media;

import java.util.Date;

import com.kiwisoft.utils.Identifyable;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.dataImport.DataSource;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

/**
 * @author Stefan Stiller
 * @todo handle person schedules
 */
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
	public static final String DETAILS_LINK="detailsLink";

	private String event;
	private boolean reminder;
	private Date date;
	private String detailsLink;

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
		setModified(EVENT, oldEvent, event);
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
				if (event!=null) return show.getTitle(language)+" - "+episode.getTitleWithKey(language)+" "+event;
				else  return show.getTitle(language)+" - "+episode.getTitleWithKey(getLanguage());
			}
			else if (event!=null) return show.getTitle(language)+" - "+event;
			else return show.getTitle(language);
		}
		return event;
	}

	public boolean isReminder()
	{
		return reminder;
	}

	public void setReminder(boolean reminder)
	{
		boolean oldReminder=this.reminder;
		this.reminder=reminder;
		setModified("reminder", oldReminder, this.reminder);
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
		setModified(DATE, oldDate, date);
	}

	public Channel getChannel()
	{
		return (Channel)getReference(CHANNEL);
	}

	public void setChannel(Channel channel)
	{
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


	public String getDetailsLink()
	{
		return detailsLink;
	}

	public void setDetailsLink(String detailsLink)
	{
		String oldDetailsLink=this.detailsLink;
		this.detailsLink=detailsLink;
		firePropertyChange(DETAILS_LINK, oldDetailsLink, this.detailsLink);
	}

	public Identifyable loadReference(String name, Object referenceId)
	{
		if (DATA_SOURCE.equals(name)) return DataSource.valueOf((Long)referenceId);
		return super.loadReference(name, referenceId);
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
