package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.persistence.DBLoader;

public class GenreManager
{
	private static GenreManager instance;

	public synchronized static GenreManager getInstance()
	{
		if (instance==null) instance=new GenreManager();
		return instance;
	}

	private GenreManager()
	{
	}

	public Set<Genre> getGenres()
	{
		return DBLoader.getInstance().loadSet(Genre.class);
	}

	public Genre getGenre(Long id)
	{
		return DBLoader.getInstance().load(Genre.class, id);
	}
}
