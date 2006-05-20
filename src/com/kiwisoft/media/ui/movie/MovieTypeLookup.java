/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 5, 2003
 * Time: 12:04:31 PM
 */
package com.kiwisoft.media.ui.movie;

import java.util.Collection;

import com.kiwisoft.media.movie.MovieType;
import com.kiwisoft.utils.gui.lookup.ListLookup;

public class MovieTypeLookup extends ListLookup<MovieType>
{
	public Collection<MovieType> getValues(String text, MovieType currentValue)
	{
		if (text==null) return MovieType.getAll();
		else
		{
			if (text.indexOf('*')<0) text=text+"*";
			Collection<MovieType> values=MovieType.getAll(text);
			if (values.size()==1 && currentValue!=null && values.contains(currentValue))
				return MovieType.getAll();
			return values;
		}
	}

}
