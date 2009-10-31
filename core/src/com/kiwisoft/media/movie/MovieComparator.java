package com.kiwisoft.media.movie;

import java.util.Comparator;

import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class MovieComparator implements Comparator<Movie>
{
	@Override
	public int compare(Movie movie1, Movie movie2)
	{
		int result=Utils.compareNullSafe(movie1.getIndexBy(), movie2.getIndexBy(), false);
		if (result==0) result=Utils.compareNullSafe(movie1.getYear(), movie2.getYear(), false);
		if (result==0) result=movie1.getId().compareTo(movie2.getId());
		return result;
	}
}
