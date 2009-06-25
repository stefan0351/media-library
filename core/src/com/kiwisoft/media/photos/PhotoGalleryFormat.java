package com.kiwisoft.media.photos;

import com.kiwisoft.format.DefaultObjectFormat;

public class PhotoGalleryFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object value)
	{
		if (value instanceof PhotoGallery)
		{
			return ((PhotoGallery)value).getName();
		}
		return super.format(value);
	}
}
