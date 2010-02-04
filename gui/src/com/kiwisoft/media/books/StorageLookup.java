package com.kiwisoft.media.books;

import com.kiwisoft.swing.lookup.StringLookup;

import java.util.Collection;

/**
 * @author Stefan Stiller
 * @since 02.12.2009
 */
public class StorageLookup extends StringLookup
{
	@Override
	public Collection<String> getAllStrings()
	{
		return BookManager.getInstance().getStorages();
	}
}