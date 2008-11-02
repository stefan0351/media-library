package com.kiwisoft.media.person;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class GenderFormat extends DefaultObjectFormat
{
	public String format(Object value)
	{
		if (value instanceof Gender)
		{
			Gender gender=(Gender)value;
			return gender.getName();
		}
		return super.format(value);
	}

	public String getIconName(Object value)
	{
		if (value instanceof Gender)
		{
			Gender gender=(Gender)value;
			if (gender==Gender.FEMALE) return "female";
			else if (gender==Gender.MALE) return "male";
			return null;
		}
		return super.getIconName(value);
	}

}
