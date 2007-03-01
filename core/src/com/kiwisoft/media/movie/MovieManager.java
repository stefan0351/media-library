/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 12, 2003
 * Time: 1:08:16 PM
 */
package com.kiwisoft.media.movie;

import java.util.Collection;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.SearchPattern;

public class MovieManager
{
	public static final String MOVIES="movies";

	private static MovieManager instance;

	public synchronized static MovieManager getInstance()
	{
		if (instance==null) instance=new MovieManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private MovieManager()
	{
	}

	public Collection<Movie> getMovies()
	{
		return DBLoader.getInstance().loadSet(Movie.class);
	}

	public Movie getMovie(Long id)
	{
		return DBLoader.getInstance().load(Movie.class, id);
	}

	public Movie getMovieByName(String name)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		Movie movie=dbLoader.load(Movie.class, null, "name=?", name);
		if (movie==null)
		{
			movie=dbLoader.load(Movie.class, "names", "names.type=? and names.ref_id=shows.id and names.name=?", Name.MOVIE, name);
		}
		return movie;
	}

	public boolean isMovieUsed(Movie movie)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		return (dbLoader.count(Movie.class, null, "movie_id=?", movie.getId())>0)
		        || (dbLoader.count(Airdate.class, null, "movie_id=?", movie.getId())>0);
	}

	public Movie createMovie(Show show)
	{
		Movie movie=new Movie(show);
		fireElementAdded(MOVIES, movie);
		return movie;
	}

	public void dropMovie(Movie movie)
	{
		movie.delete();
		fireElementRemoved(MOVIES, movie);
	}

	public SearchPattern getSearchPattern(int type, Movie movie)
	{
		return DBLoader.getInstance().load(SearchPattern.class, null, "type=? and movie_id=?", type, movie.getId());
	}

	public void addCollectionChangeListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	protected void fireElementAdded(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementAdded(propertyName, element);
	}

	protected void fireElementRemoved(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementRemoved(propertyName, element);
	}

}
