package com.kiwisoft.media;

import javax.swing.Icon;

import com.kiwisoft.utils.gui.format.DefaultObjectFormat;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.media.Language;

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
			return IconManager.getIcon("com/kiwisoft/media/icons/flag_"+language.getSymbol()+".gif");
		}
		return super.getIcon(value);
	}
}
