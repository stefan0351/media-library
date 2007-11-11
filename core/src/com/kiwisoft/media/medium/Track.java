/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media.medium;

import com.kiwisoft.collection.ChainLink;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.persistence.IDObject;

public class Track extends IDObject implements ChainLink, Recordable
{
	public static final String SHOW="show";
	public static final String EPISODE="episode";
	public static final String MOVIE="movie";
	public static final String LANGUAGE="language";
	public static final String MEDIUM="medium";
	public static final String SEQUENCE="sequence";

	private String event;
	private int length;
	private boolean longPlay;
	private int sequence;

	public Track(Medium video)
	{
		setMedium(video);
	}

	public Track(DBDummy dummy)
	{
		super(dummy);
	}

	public int getLength()
	{
		return length;
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

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language language)
	{
		setReference(LANGUAGE, language);
	}

	public void setLength(int length)
	{
		Medium video=getMedium();
		if (video!=null)
		{
			MediumType type=video.getType();
			if (type!=null && type.isRewritable())
			{
				int remaining=video.getRemainingLength();
				remaining=remaining+this.length-length;
				if (remaining<0) remaining=0;
				video.setRemainingLength(remaining);
			}
		}
		this.length=length;
		setModified();
	}

	public String getEvent()
	{
		return event;
	}

	public void setEvent(String event)
	{
		this.event=event;
		setModified();
	}

	public boolean isLongPlay()
	{
		return longPlay;
	}

	public void setLongPlay(boolean longPlay)
	{
		this.longPlay=longPlay;
		setModified();
	}

	public Medium getMedium()
	{
		return (Medium)getReference(MEDIUM);
	}

	public void setMedium(Medium medium)
	{
		setReference(MEDIUM, medium);
	}

	public int getSequence()
	{
		return sequence;
	}

	public void setSequence(int sequence)
	{
		int oldSequence=getSequence();
		this.sequence=sequence;
		setModified();
		firePropertyChange(SEQUENCE, oldSequence, sequence);
	}

	public void setChainPosition(int position)
	{
		setSequence(position);
	}

	public int getChainPosition()
	{
		return getSequence();
	}

	public String getName()
	{
		Language language=getLanguage();
		return getName(language);
	}

	private String getName(Language language)
	{
		StringBuilder name=new StringBuilder();
		Movie movie=getMovie();
		if (movie!=null) name.append(movie.getTitle(language));
		else
		{
			Episode episode=getEpisode();
			if (episode!=null)
			{
				name.append(episode.getShow().getTitle(language));
				name.append(": ");
				name.append(episode.getTitleWithKey(language));
			}
			else
			{
				Show show=getShow();
				if (show!=null) name.append(show.getTitle(language));
			}
		}
		String event=getEvent();
		if (!StringUtils.isEmpty(event))
		{
			if (name.length()>0) name.append(" - ");
			name.append(event);
		}
		return name.toString();
	}

	public String toString()
	{
		return getName();
	}

	public static class Comparator implements java.util.Comparator
	{
		public int compare(Object o1, Object o2)
		{
			Track e1=(Track)o1;
			Track e2=(Track)o2;
			if (e1.getSequence()<e2.getSequence()) return -1;
			if (e1.getSequence()>e2.getSequence()) return 1;
			return 0;
		}
	}

	public int getRecordableLength()
	{
		return getLength();
	}

	public String getRecordableName(Language language)
	{
		return getName(language);
	}

	public void initRecord(Track track)
	{
		track.setShow(getShow());
		track.setEpisode(getEpisode());
		track.setMovie(getMovie());
		track.setEvent(getEvent());
		track.setLongPlay(isLongPlay());
	}
}
