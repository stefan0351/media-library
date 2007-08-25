package com.kiwisoft.media;

import java.util.Collection;
import java.util.Collections;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.persistence.DBLoader;

public class CountryLookup extends ListLookup<Country>
{
	public Collection<Country> getValues(String text, Country currentValue, boolean lookup)
	{
		if (lookup) return DBLoader.getInstance().loadSet(Country.class);
		if (text==null) return Collections.emptySet();
		String name;
		if (text.indexOf('*')>=0) name=text.replace('*', '%');
		else name=text+"%";
		return DBLoader.getInstance().loadSet(Country.class, null, "name like ? or symbol=?", name, text);
	}
}
