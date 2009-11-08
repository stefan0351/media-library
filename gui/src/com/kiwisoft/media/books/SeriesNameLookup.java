package com.kiwisoft.media.books;

import com.kiwisoft.swing.lookup.StringLookup;

import java.util.Collection;

/**
 * @author Stefan Stiller
 * @since 27.10.2009
 */
public class SeriesNameLookup extends StringLookup
{
	@Override
	public Collection<String> getAllStrings()
	{
		return BookManager.getInstance().getSeriesNames();
	}
}