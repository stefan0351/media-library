package com.kiwisoft.media.medium;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.swing.lookup.LookupUtils;

public class TrackTypeLookup extends ListLookup<TrackType>
{
	@Override
	public Collection<TrackType> getValues(String text, TrackType currentValue, int lookup)
	{
		if (lookup>0) return TrackType.values();
		Set<TrackType> values=new HashSet<TrackType>();
		Pattern pattern=LookupUtils.createPattern(text);
		for (TrackType type : TrackType.values())
		{
			if (pattern.matcher(type.getName()).matches()) values.add(type);
		}
		return values;
	}
}
