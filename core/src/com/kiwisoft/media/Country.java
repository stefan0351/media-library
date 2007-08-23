package com.kiwisoft.media;

import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

public class Country extends IDObject implements Comparable
{
	private String symbol;
	private String name;

	public Country()
	{
	}

	public Country(DBDummy dummy)
	{
		super(dummy);
	}

	public String getSymbol()
	{
		return symbol;
	}

	public void setSymbol(String symbol)
	{
		this.symbol=symbol;
		setModified();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
		setModified();
	}

	public String toString()
	{
		return getName();
	}

	public int compareTo(Object o)
	{
		Country country=(Country)o;
		return name.compareToIgnoreCase(country.name);
	}
}
