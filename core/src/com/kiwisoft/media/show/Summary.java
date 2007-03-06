package com.kiwisoft.media.show;

import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.movie.Movie;

public class Summary extends IDObject
{
	public static final String MOVIE="movie";
	public static final String EPISODE="episode";
	public static final String LANGUAGE="language";
	public static final String SUMMARY="summary";

	private String summary;

	public Summary()
	{
	}

	public Summary(DBDummy dummy)
	{
		super(dummy);
	}

	public Episode getEpisode()
	{
		return (Episode)getReference(EPISODE);
	}

	public void setEpisode(Episode value)
	{
		setReference(EPISODE, value);
	}

	public Movie getMovie()
	{
		return (Movie)getReference(MOVIE);
	}

	public void setMovie(Movie value)
	{
		setReference(MOVIE, value);
	}

	public Language getLanguage()
	{
		return (Language)getReference(LANGUAGE);
	}

	public void setLanguage(Language value)
	{
		setReference(LANGUAGE, value);
	}

	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String value)
	{
		String oldSummary=getSummary();
		this.summary=value;
		setModified();
		firePropertyChange(SUMMARY, oldSummary, summary);
	}
}
