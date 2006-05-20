/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.movie;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.WebInfo;

public class MovieInfo extends WebInfo
{
	public static final String MOVIE="movie";

	public MovieInfo(Movie movie)
	{
		setMovie(movie);
	}

	public MovieInfo(DBDummy dummy)
	{
		super(dummy);
	}

	public Movie getMovie()
	{
		return (Movie)getReference(MOVIE);
	}

	public void setMovie(Movie movie)
	{
		setReference(MOVIE, movie);
	}

	public boolean isDefault()
	{
		return getMovie().getDefaultInfo()==this;
	}
}
