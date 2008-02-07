package com.kiwisoft.media.download;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class WebFolderFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object value)
	{
		if (value instanceof WebFolder)
		{
			WebFolder webFolder=(WebFolder)value;
			return webFolder.getName();
		}
		return super.format(value);
	}


	@Override
	public String getIconName(Object value)
	{
		if (value instanceof WebFolder)
		{
			WebFolder folder=(WebFolder)value;
			if (folder.getParent()!=null) return "webdirectory";
			return "webhost";
		}
		return super.getIconName(value);
	}
}
