package com.kiwisoft.media;

import com.kiwisoft.collection.SetMap;
import com.kiwisoft.collection.SortedSetMap;
import com.kiwisoft.format.FormatStringComparator;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieComparator;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowComparator;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.StringUtils;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class SearchAction extends BaseAction
{
	private String type;
	private String text;
	private Set<Book> books;
	private Set<Show> shows;
	private SetMap<Show, Episode> episodes;
	private Set<Movie> movies;
	private Set<Person> persons;

	@Override
	public String getPageTitle()
	{
		return "Search";
	}

	@Override
	public String execute() throws Exception
	{
		text=StringUtils.trimAll(text);
		if (!StringUtils.isEmpty(text) && text.length()>1)
		{
			String searchText;
			if (text.contains("*") || text.contains("?")) searchText=text.replace("*", "%").replace("?", "_");
			else searchText="%"+text+"%";
			if ("shows".equals(type) || "all".equals(type))
			{
				shows=new TreeSet<Show>(new ShowComparator());
				shows.addAll(DBLoader.getInstance().loadSet(Show.class, null, "title like ? or german_title like ?", searchText, searchText));
				shows.addAll(DBLoader.getInstance().loadSet(Show.class, "names", "names.type=? and names.ref_id=shows.id and names.name like ?",
															Name.SHOW, searchText));
			}
			if ("episodes".equals(type) || "all".equals(type))
			{
				episodes=new SortedSetMap<Show, Episode>(new ShowComparator(), null);
				for (Episode episode : DBLoader.getInstance().loadSet(Episode.class, null, "title like ? or german_title like ?", searchText, searchText))
				{
					episodes.add(episode.getShow(), episode);
				}
				for (Episode episode : DBLoader.getInstance().loadSet(Episode.class, "names",
																	  "names.type=? and names.ref_id=episodes.id and names.name like ?",
																	  Name.EPISODE, searchText))
				{
					episodes.add(episode.getShow(), episode);
				}
			}
			if ("movies".equals(type) || "all".equals(type))
			{
				movies=new TreeSet<Movie>(new MovieComparator());
				movies.addAll(DBLoader.getInstance().loadSet(Movie.class, null, "title like ? or german_title like ?", searchText, searchText));
				movies.addAll(DBLoader.getInstance().loadSet(Movie.class, "names", "names.type=? and names.ref_id=movies.id and names.name like ?",
															 Name.MOVIE, searchText));
			}
			if ("persons".equals(type) || "all".equals(type))
			{
				persons=new TreeSet<Person>(new FormatStringComparator());
				persons.addAll(DBLoader.getInstance().loadSet(Person.class, null, "name like ?", searchText));
				persons.addAll(DBLoader.getInstance().loadSet(Person.class, "names", "names.type=? and names.ref_id=persons.id and names.name like ?",
															  Name.PERSON, searchText));
			}
			if ("books".equals(type) || "all".equals(type))
			{
				books=new TreeSet<Book>(new FormatStringComparator());
				books.addAll(DBLoader.getInstance().loadSet(Book.class, null, "title like ? or series_name like ?", searchText, searchText));
			}
		}
		return super.execute();
	}

	public boolean isNothingFound()
	{
		return (persons==null || persons.isEmpty())
			   && (shows==null || shows.isEmpty())
			   && (episodes==null || episodes.isEmpty())
			   && (movies==null || movies.isEmpty())
			   && (books==null || books.isEmpty());
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type=type;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text=text;
	}

	public Set<Show> getShows()
	{
		return shows;
	}

	public void setShows(Set<Show> shows)
	{
		this.shows=shows;
	}

	public SetMap<Show, Episode> getEpisodes()
	{
		return episodes;
	}

	public Set<Episode> getEpisodes(Show show)
	{
		return episodes.get(show);
	}

	public void setEpisodes(SetMap<Show, Episode> episodes)
	{
		this.episodes=episodes;
	}

	public Set<Movie> getMovies()
	{
		return movies;
	}

	public void setMovies(Set<Movie> movies)
	{
		this.movies=movies;
	}

	public Set<Person> getPersons()
	{
		return persons;
	}

	public void setPersons(Set<Person> persons)
	{
		this.persons=persons;
	}

	public Set<Book> getBooks()
	{
		return books;
	}

	public void setBooks(Set<Book> books)
	{
		this.books=books;
	}
}
