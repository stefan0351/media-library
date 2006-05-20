/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 29, 2003
 * Time: 12:21:05 AM
 */
package com.kiwisoft.media;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;

public class SearchPattern extends IDObject
{
	public static final String SHOW="show";
	public static final String MOVIE="movie";
	public static final String ACTOR="actor";

	public static final int PRISMA_ONLINE=1;
	public static final int TVTV=2;

	private String pattern;
	private int type;

	public SearchPattern(Show show, int type)
	{
		setShow(show);
		setType(type);
	}

	public SearchPattern(Movie movie, int type)
	{
		setMovie(movie);
		setType(type);
	}

	public SearchPattern(Person person, int type)
	{
		setActor(person);
		setType(type);
	}

	public SearchPattern(DBDummy dummy)
	{
		super(dummy);
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type=type;
		setModified();
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern=pattern;
		setModified();
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public Movie getMovie()
	{
		return (Movie)getReference(MOVIE);
	}

	public void setMovie(Movie value)
	{
		setReference(MOVIE, value);
	}

	public Person getActor()
	{
		return (Person)getReference(ACTOR);
	}

	public void setActor(Person value)
	{
		setReference(ACTOR, value);
	}

	public void afterReload()
	{
		super.afterReload();
	}
}
