package com.kiwisoft.media.medium;

import java.util.*;
import java.util.regex.Pattern;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.swing.lookup.LookupUtils;
import com.kiwisoft.media.medium.MediumType;

/**
 * @author Stefan Stiller
 */
public class MediumTypeLookup extends ListLookup<MediumType>
{
	public Collection<MediumType> getValues(String text, MediumType currentValue, boolean lookup)
	{
		if (lookup) return MediumType.values();
		if (text==null) return Collections.emptySet();
		Set<MediumType> values=new HashSet<MediumType>();
		Pattern pattern=LookupUtils.createPattern(text);
		for (MediumType type : MediumType.values())
		{
			if (pattern.matcher(type.getId().toString()).matches() || pattern.matcher(type.getName()).matches()) values.add(type);
		}
		return values;
	}
}
