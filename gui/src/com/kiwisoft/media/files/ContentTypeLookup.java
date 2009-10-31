package com.kiwisoft.media.files;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.swing.lookup.LookupUtils;

public class ContentTypeLookup extends ListLookup<ContentType>
{
	private MediaType mediaType;

	public ContentTypeLookup(MediaType mediaType)
	{
		this.mediaType=mediaType;
	}

	@Override
	public Collection<ContentType> getValues(String text, ContentType currentValue, int lookup)
	{
		if (lookup>0) return ContentType.values(mediaType);
		if (text==null) return Collections.emptySet();
		Set<ContentType> values=new HashSet<ContentType>();
		Pattern pattern=LookupUtils.createPattern(text);
		for (ContentType contentType : ContentType.values(mediaType))
		{
			if (pattern.matcher(contentType.getName()).matches()) values.add(contentType);
		}
		return values;
	}
}
