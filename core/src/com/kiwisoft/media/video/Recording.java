/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media.video;

import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;

public class Recording extends IDObject implements Chain.ChainLink
{
	public static final String SHOW="show";
	public static final String EPISODE="episode";
	public static final String MOVIE="movie";
	public static final String LANGUAGE="language";
	public static final String VIDEO="video";
	public static final String SEQUENCE="sequence";

	private String event;
	private int length;
	private boolean longPlay;
	private int sequence;

	public Recording(Video video)
	{
		setVideo(video);
	}

	public Recording(DBDummy dummy)
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
		Video video=getVideo();
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

	public Video getVideo()
	{
		return (Video)getReference(VIDEO);
	}

	public void setVideo(Video video)
	{
		setReference(VIDEO, video);
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

	public String toString()
	{
		return getName();
	}

	public static class Comparator implements java.util.Comparator
	{
		public int compare(Object o1, Object o2)
		{
			Recording e1=(Recording)o1;
			Recording e2=(Recording)o2;
			if (e1.getSequence()<e2.getSequence()) return -1;
			if (e1.getSequence()>e2.getSequence()) return 1;
			return 0;
		}
	}
}
