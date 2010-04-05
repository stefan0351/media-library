package com.kiwisoft.media.files;

import com.kiwisoft.format.ObjectFormat;
import com.kiwisoft.swing.icons.IconManager;

import javax.swing.*;

/**
 * @author Stefan Stiller
 * @since 05.04.2010
 */
public class MediaFileIconFormat implements ObjectFormat
{
	@Override
	public boolean canParse(Class aClass)
	{
		return false;
	}

	@Override
	public Object format(Object o)
	{
		if (o instanceof MediaFile)
		{
			MediaFile mediaFile=(MediaFile) o;
			return IconManager.getIconFromFile(mediaFile.getPhysicalFile().getAbsolutePath());
		}
		return null;
	}

	@Override
	public String getIconName(Object o)
	{
		return null;
	}

	@Override
	public int getHorizontalAlignment(Object value)
	{
		return SwingConstants.CENTER;
	}
}
