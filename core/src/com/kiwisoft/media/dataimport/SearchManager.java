package com.kiwisoft.media.dataImport;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.persistence.DBLoader;

public class SearchManager
{
	private static SearchManager instance;

	public synchronized static SearchManager getInstance()
	{
		if (instance==null) instance=new SearchManager();
		return instance;
	}

	private SearchManager()
	{
	}

	public Collection<SearchPattern> getSearchPatterns(int type, Class refClass)
	{
		if (refClass==Show.class) return DBLoader.getInstance().loadSet(SearchPattern.class, null,
				"type=? and show_id is not null", type);
		if (refClass==Movie.class) return DBLoader.getInstance().loadSet(SearchPattern.class, null,
				"type=? and movie_id is not null", type);
		if (refClass==Person.class) return DBLoader.getInstance().loadSet(SearchPattern.class, null,
						"type=? and actor_id is not null", type);
		return Collections.emptySet();
	}

	public Set<SearchPattern> getSearchPattern(int type)
	{
		return DBLoader.getInstance().loadSet(SearchPattern.class, null, "type=?", type);
	}

	public Set<SearchPattern> getSearchPattern(int type, Show show)
	{
		return DBLoader.getInstance().loadSet(SearchPattern.class, null, "type=? and show_id=?", type, show.getId());
	}

	public Set<SearchPattern> getSearchPattern(int type, Movie movie)
	{
		return DBLoader.getInstance().loadSet(SearchPattern.class, null, "type=? and movie_id=?", type, movie.getId());
	}

	public Set<SearchPattern> getSearchPattern(int type, Person person)
	{
		return DBLoader.getInstance().loadSet(SearchPattern.class, null, "type=? and actor_id=?", type, person.getId());
	}

}
