package com.kiwisoft.media.medium;

import java.util.Comparator;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
*/
public class TracksByTitleComparator implements Comparator<Track>
{
	@Override
	public int compare(Track o1, Track o2)
	{
		Movie movie1=o1.getMovie();
		Movie movie2=o2.getMovie();
		int result=Utils.compareNullSafe(movie1.getIndexBy(o1.getLanguage()), movie2.getIndexBy(o2.getLanguage()), true);
		if (result==0) result=Utils.compareNullSafe(o1.getEvent(), o2.getEvent(), true);
		if (result==0) result=Utils.compareNullSafe(movie1.getYear(), movie2.getYear(), false);
		if (result==0) result=o1.getId().compareTo(o2.getId());
		return result;
	}
}
