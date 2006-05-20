/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.ui.movie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieManager;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class MovieLookup extends ListLookup<Movie>
{
	public Collection<Movie> getValues(String text, Movie currentValue)
	{
		if (text==null) return MovieManager.getInstance().getMovies();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			Set<Movie> movies=new HashSet<Movie>();
			DBLoader dbLoader=DBLoader.getInstance();
			movies.addAll(dbLoader.loadSet(Movie.class, null, "name like ?", text));
			movies.addAll(dbLoader.loadSet(Movie.class, "names", "names.ref_id=movies.id and names.name like ?", text));
			return movies;
		}
	}

}
