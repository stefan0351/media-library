package com.kiwisoft.media.medium;

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
		return MediumManager.getInstance().getStorages();
	}
}
