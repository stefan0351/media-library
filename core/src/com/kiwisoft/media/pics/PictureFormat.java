package com.kiwisoft.media.pics;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class PictureFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object value)
	{
		if (value instanceof Picture)
		{
			Picture picture=(Picture)value;
			return picture.getName();
		}
		return super.format(value);
	}
}
