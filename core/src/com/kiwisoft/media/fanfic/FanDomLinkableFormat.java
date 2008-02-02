package com.kiwisoft.media.fanfic;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class FanDomLinkableFormat extends DefaultObjectFormat
{
	@Override
	public String format(Object object)
	{
		if (object instanceof FanDom)
		{
			FanDom fanDom=(FanDom)object;
			return fanDom.getName()+" - FanFic";
		}
		return super.format(object);
	}
}
