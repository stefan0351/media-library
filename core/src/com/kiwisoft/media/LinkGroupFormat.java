package com.kiwisoft.media;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class LinkGroupFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object object)
	{
		if (object instanceof LinkGroup)
		{
			LinkGroup linkGroup=(LinkGroup)object;
			return linkGroup.getName();
		}
		return super.format(object);
	}

	@Override
	public String getIconName(Object object)
	{
		return "linkgroup";
	}
}
