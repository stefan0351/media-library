package com.kiwisoft.media.person;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.swing.lookup.LookupUtils;

/**
 * @author Stefan Stiller
 */
public class CreditTypeLookup extends ListLookup<CreditType>
{
	@Override
	public Collection<CreditType> getValues(String text, CreditType currentValue, int lookup)
	{
		if (lookup>0) return CreditType.noCastValues();
		if (text==null) return Collections.emptySet();
		Set<CreditType> values=new HashSet<CreditType>();
		Pattern pattern=LookupUtils.createPattern(text);
		for (CreditType creditType : CreditType.noCastValues())
		{
			if (pattern.matcher(creditType.getAsName()).matches() || pattern.matcher(creditType.getByName()).matches()) values.add(creditType);
		}
		return values;
	}
}
