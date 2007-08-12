package com.kiwisoft.media.medium;

import java.util.Collection;

import com.kiwisoft.utils.gui.lookup.ListLookup;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.media.medium.MediumType;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 24.04.2004
 * Time: 17:14:29
 * To change this template use File | Settings | File Templates.
 */
public class MediumTypeLookup extends ListLookup<MediumType>
{
	public Collection<MediumType> getValues(String text, MediumType currentValue, boolean lookup)
	{
		if (StringUtils.isEmpty(text)) return MediumType.getAll();
		else
		{
			if (!text.contains("*")) text=text+"*";
			return MediumType.getAll(text);
		}
	}
}
