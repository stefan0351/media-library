package com.kiwisoft.media;

import javax.swing.Icon;

import com.kiwisoft.utils.gui.format.DefaultObjectFormat;
import com.kiwisoft.utils.gui.IconManager;

public class CountryFormat extends DefaultObjectFormat
{
	public CountryFormat()
	{
		super(DEFAULT);
	}

	public String format(Object value)
	{
		if (value instanceof Country)
		{
			Country country=(Country)value;
			return country.getName();
		}
		return super.format(value);
	}

	public Icon getIcon(Object value)
	{
		if (value instanceof Country)
		{
			Country country=(Country)value;
			return IconManager.getIcon("com/kiwisoft/media/icons/countries/"+country.getSymbol()+".png");
		}
		return super.getIcon(value);
	}
}
