package com.kiwisoft.media;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.kiwisoft.persistence.DBLoader;

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

	public Map<String, Country> symbolMap=new HashMap<String, Country>();

	public Country getCountryBySymbol(String symbol)
	{
		if (symbolMap.containsKey(symbol))
		{
			return symbolMap.get(symbol);
		}
		else
		{
			Country country=DBLoader.getInstance().load(Country.class, null, "symbol=?", symbol);
			symbolMap.put(symbol, country);
			return country;
		}
	}

	public Country getCountryByName(String name)
	{
		return DBLoader.getInstance().load(Country.class, null, "name=?", name);
	}
	
	public Country createCountry(String symbol, String name)
	{
		Country country=new Country();
		country.setSymbol(symbol);
		country.setName(name);
		return country;
	}
}
