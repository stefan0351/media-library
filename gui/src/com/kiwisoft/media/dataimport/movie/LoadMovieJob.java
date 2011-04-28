package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.media.dataimport.IMDbComLoader;
import com.kiwisoft.media.dataimport.MovieData;
import com.kiwisoft.progress.Job;
import com.kiwisoft.progress.ProgressListener;

import java.io.IOException;

/**
* @author Stefan Stiller
* @since 27.02.11
*/
public class LoadMovieJob implements Job
{
	private IMDbComLoader loader;
	private MovieData movieData;

	public LoadMovieJob(IMDbComLoader loader)
	{
		this.loader=loader;
	}

	@Override
	public boolean run(ProgressListener progressListener) throws Exception
	{
		movieData=loader.load();
		return true;
	}

	public MovieData getMovieData()
	{
		return movieData;
	}

	@Override
	public String getName()
	{
		return "Load Movie";
	}

	@Override
	public void dispose() throws IOException
	{
	}
}
