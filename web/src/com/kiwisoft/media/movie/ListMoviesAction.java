package com.kiwisoft.media.movie;

import com.kiwisoft.media.BaseAction;

import java.util.SortedSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Stefan Stiller
 * @since 30.09.2009
 */
public class ListMoviesAction extends BaseAction
{
	private Character letter;
	private SortedSet<Character> letters;
	private Set<Movie> movies;

	public ListMoviesAction()
	{
	}

	@Override
	public String getPageTitle()
	{
		return "Movies";
	}

	@Override
	public String execute() throws Exception
	{
		letters=MovieManager.getInstance().getLetters();
		if (letter==null)
		{
			if (letters.size()>1) letter=letters.first();
			else letter='A';
		}
		movies=new TreeSet<Movie>(new MovieComparator());
		movies.addAll(MovieManager.getInstance().getMoviesByLetter(letter));
		return super.execute();
	}

	public SortedSet<Character> getLetters()
	{
		return letters;
	}

	public Set<Movie> getMovies()
	{
		return movies;
	}

	public Character getLetter()
	{
		return letter;
	}

	public void setLetter(Character letter)
	{
		this.letter=letter;
	}
}
