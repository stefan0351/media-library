package com.kiwisoft.media;

import java.util.Set;

import com.kiwisoft.utils.db.DBLoader;

public class CountryManager
{
	private static CountryManager instance;

	public synchronized static CountryManager getInstance()
	{
		if (instance==null) instance=new CountryManager();
		return instance;
	}

	private CountryManager()
	{
	}

	public Set<Country> getCountries()
	{
		return DBLoader.getInstance().loadSet(Country.class);
	}

	public Country getCountryBySymbol(String symbol)
	{
		return DBLoader.getInstance().load(Country.class, null, "symbol=?", symbol);
	}
}
