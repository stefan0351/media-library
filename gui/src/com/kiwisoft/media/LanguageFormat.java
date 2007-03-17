package com.kiwisoft.media;

import javax.swing.Icon;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.format.DefaultObjectFormat;

public class LanguageFormat extends DefaultObjectFormat
{
	public LanguageFormat()
	{
		super(DEFAULT);
	}

	public String format(Object value)
	{
		if (value instanceof Language)
		{
			Language language=(Language)value;
			return language.getName();
		}
		return super.format(value);
	}

	public Icon getIcon(Object value)
	{
		if (value instanceof Language)
		{
			Language language=(Language)value;
			return IconManager.getIcon("com/kiwisoft/media/icons/languages/"+language.getSymbol()+".png");
		}
		return super.getIcon(value);
	}
}
