package com.kiwisoft.media.download;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class WebDocumentFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object value)
	{
		if (value instanceof WebDocument)
		{
			WebDocument document=(WebDocument)value;
			return	document.getURL().toString();
		}
		return super.format(value);
	}

	@Override
	public String getIconName(Object value)
	{
		if (value instanceof WebDocument)
		{
			WebDocument document=(WebDocument)value;
			return document.getState().getIcon();
		}
		return super.getIconName(value);
	}
}
