package com.kiwisoft.media.movie;

import com.kiwisoft.swing.table.SortableTableRow;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Stefan Stiller
* @since 24.10.2009
*/
public class MovieTableRow extends SortableTableRow<Movie> implements PropertyChangeListener
{
	public MovieTableRow(Movie movie)
	{
		super(movie);
	}

	@Override
	public void installListener()
	{
		getUserObject().addPropertyChangeListener(this);
	}

	@Override
	public void removeListener()
	{
		getUserObject().removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		fireRowUpdated();
	}

	@Override
	public Object getDisplayValue(int column, String property)
	{
		if ("title".equals(property)) return getUserObject().getTitle();
		else if ("germanTitle".equals(property)) return getUserObject().getGermanTitle();
		else if ("year".equals(property)) return getUserObject().getYear();
		else if ("poster".equals(property)) return getUserObject().hasPoster();
		return null;
	}
}
