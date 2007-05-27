package com.kiwisoft.media.photos;

import javax.swing.Icon;

import com.kiwisoft.utils.format.DefaultObjectFormat;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.media.Language;

public class PhotoGalleryFormat extends DefaultObjectFormat
{
	public PhotoGalleryFormat()
	{
		super(DEFAULT);
	}

	public String format(Object value)
	{
		if (value instanceof PhotoGallery)
		{
			return ((PhotoGallery)value).getName();
		}
		return super.format(value);
	}
}
