package com.kiwisoft.media.person;

import javax.swing.Icon;

import com.kiwisoft.utils.format.DefaultObjectFormat;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 13:58:31
 * To change this template use File | Settings | File Templates.
 */
public class GenderFormat extends DefaultObjectFormat
{
	public GenderFormat()
	{
		super(DEFAULT);
	}

	public String format(Object value)
	{
		if (value instanceof Gender)
		{
			Gender gender=(Gender)value;
			return gender.getName();
		}
		return super.format(value);
	}

	public Icon getIcon(Object value)
	{
		if (value instanceof Gender)
		{
			Gender gender=(Gender)value;
			if (gender==Gender.FEMALE) return Icons.getIcon("female");
			else if (gender==Gender.MALE) return Icons.getIcon("male");
			return null;
		}
		return super.getIcon(value);
	}

}
