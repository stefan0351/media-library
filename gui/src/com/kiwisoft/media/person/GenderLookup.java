package com.kiwisoft.media.person;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.kiwisoft.utils.gui.lookup.ListLookup;
import com.kiwisoft.utils.gui.lookup.LookupUtils;

public class GenderLookup extends ListLookup<Gender>
{
	public Collection<Gender> getValues(String text, Gender currentValue, boolean lookup)
	{
		if (lookup) return Gender.values();
		if (text==null) return Collections.emptySet();
		Set<Gender> values=new HashSet<Gender>();
		Pattern pattern=LookupUtils.createPattern(text);
		for (Gender gender : Gender.values())
		{
			if (pattern.matcher(gender.getName()).matches()) values.add(gender);
		}
		return values;
	}
}
