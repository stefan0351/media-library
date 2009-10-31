/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 11:26:28 PM
 */
package com.kiwisoft.media;

import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

public class Language extends IDObject implements Comparable
{
	public static final String NAME="name";
	public static final String SYMBOL="symbol";

	private String symbol;
	private String name;

	public Language()
	{
	}

	public Language(DBDummy dummy)
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
		setModified(SYMBOL, oldSymbol, this.symbol);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, this.name);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public int compareTo(Object o)
	{
		Language language=(Language)o;
		return name.compareToIgnoreCase(language.name);
	}
}
