/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 12, 2003
 * Time: 1:08:16 PM
 */
package com.kiwisoft.media.movie;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.dataImport.SearchPattern;

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

	public Set<Movie> getMovies()
	{
		return DBLoader.getInstance().loadSet(Movie.class);
	}

	public Movie getMovie(Long id)
	{
		return DBLoader.getInstance().load(Movie.class, id);
	}

	public Movie getMovieByTitle(String title)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		Movie movie=dbLoader.load(Movie.class, null, "title=? or german_title=?", title, title);
		if (movie==null)
		{
			movie=dbLoader.load(Movie.class, "names", "names.type=? and names.ref_id=movies.id and names.name=?", Name.MOVIE, title);
		}
		return movie;
	}

	public Movie getMovieByIMDbKey(String key)
	{
		return DBLoader.getInstance().load(Movie.class, null, "imdb_key=?", key);
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

	public SortedSet<Character> getLetters()
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct sort_letter(index_by) from movies");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next())
				{
					String string=resultSet.getString(1);
					if (string!=null && string.length()>0)
						set.add(new Character(string.charAt(0)));
				}
			}
			finally
			{
				statement.close();
			}
			return set;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Set<Movie> getMoviesByLetter(char ch)
	{
		return DBLoader.getInstance().loadSet(Movie.class, null, "sort_letter(index_by)=?", String.valueOf(ch));
	}

	public int getMovieCount()
	{
		return DBLoader.getInstance().count(Movie.class);
	}
}
