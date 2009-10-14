package com.kiwisoft.media.movie;

import com.kiwisoft.media.dataimport.MovieData;
import com.kiwisoft.media.BaseAction;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class SaveMovieAction extends BaseAction
{
	private static final long serialVersionUID=8738088653977122163L;

	private Long movieId;
	private MovieData movieData;
	private Object result;

	@Override
	public String execute()
	{
		System.out.println("ImportImdbAction.save: movieData = "+movieData);
		result=Boolean.TRUE;
		return SUCCESS;
	}

	public Object getResult()
	{
		return result;
	}

	public MovieData getMovieData()
	{
		return movieData;
	}

	public void setMovieData(MovieData movieData)
	{
		this.movieData=movieData;
	}

	public Long getMovieId()
	{
		return movieId;
	}

	public void setMovieId(Long movieId)
	{
		this.movieId=movieId;
	}
}
