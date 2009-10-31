package com.kiwisoft.media;

import java.util.Collection;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.persistence.DBLoader;

public class CountryLookup extends ListLookup<Country>
{
	@Override
	public Collection<Country> getValues(String text, Country currentValue, int lookup)
	{
		if (lookup>0) return DBLoader.getInstance().loadSet(Country.class);
		String name;
		if (text.indexOf('*')>=0) name=text.replace('*', '%');
		else name=text+"%";
		return DBLoader.getInstance().loadSet(Country.class, null, "name like ? or symbol=?", name, text);
	}
}
