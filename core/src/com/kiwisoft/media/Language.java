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
		Language language=(Language)o;
		return name.compareToIgnoreCase(language.name);
	}
}
