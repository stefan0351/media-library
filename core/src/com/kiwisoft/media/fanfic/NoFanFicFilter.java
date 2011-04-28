package com.kiwisoft.media.fanfic;

import com.kiwisoft.media.Link;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.utils.Filter;

/**
 * @author Stefan Stiller
 * @since 23.04.11
 */
public class NoFanFicFilter implements Filter<Object>
{
	@Override
	public boolean filter(Object object)
	{
		if (object instanceof Link)
		{
			Link link=(Link) object;
			String name=link.getName().toLowerCase();
			if (name.contains("fanfic") || name.contains("fan fic")) return false;
		}
		else if (object instanceof LinkGroup)
		{
			LinkGroup linkGroup=(LinkGroup) object;
			String name=linkGroup.getName().toLowerCase();
			if (name.contains("fanfic") || name.contains("fan fic")) return false;
		}
		return true;
	}
}
