package com.kiwisoft.media.medium;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.swing.lookup.LookupUtils;

public class TrackTypeLookup extends ListLookup<TrackType>
{
	public Collection<TrackType> getValues(String text, TrackType currentValue, boolean lookup)
	{
		if (lookup) return TrackType.values();
		if (text==null) return Collections.emptySet();
		Set<TrackType> values=new HashSet<TrackType>();
		Pattern pattern=LookupUtils.createPattern(text);
		for (TrackType type : TrackType.values())
		{
			if (pattern.matcher(type.getName()).matches()) values.add(type);
		}
		return values;
	}
}
