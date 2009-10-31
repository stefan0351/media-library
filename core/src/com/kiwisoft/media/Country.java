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
		String oldSymbol=this.symbol;
		this.symbol=symbol;
		setModified("symbol", oldSymbol, this.symbol);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified("name", oldName, this.name);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public int compareTo(Object o)
	{
		Country country=(Country)o;
		return name.compareToIgnoreCase(country.name);
	}
}
