package com.kiwisoft.media;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class LinkFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object object)
	{
		if (object instanceof Link)
		{
			Link link=(Link)object;
			return link.getName();
		}
		return super.format(object);
	}


	@Override
	public String getIconName(Object object)
	{
		return "link";
	}
}
