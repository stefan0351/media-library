package com.kiwisoft.media.dataimport;

import java.io.Serializable;

/**
 * @author Stefan Stiller
 * @since 10.10.2009
 */
public class LanguageData implements Serializable
{
	private static final long serialVersionUID=969491174273168827L;
	
	private String symbol;
	private String name;

	public LanguageData()
	{
	}

	public LanguageData(String name)
	{
		this.name=name;
	}

	public LanguageData(String name, String symbol)
	{
		this.name=name;
		this.symbol=symbol;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public void setSymbol(String symbol)
	{
		this.symbol=symbol;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (!(o instanceof LanguageData)) return false;

		LanguageData that=(LanguageData) o;

		if (name!=null ? !name.equals(that.name) : that.name!=null) return false;
		if (symbol!=null ? !symbol.equals(that.symbol) : that.symbol!=null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result=symbol!=null ? symbol.hashCode() : 0;
		result=31*result+(name!=null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "LanguageData{"+
			   "name='"+name+'\''+
			   ", symbol='"+symbol+'\''+
			   '}';
	}
}
