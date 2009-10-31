package com.kiwisoft.media.show;

import com.kiwisoft.media.Language;

import java.util.Comparator;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class SummaryComparator implements Comparator<Summary>
{
	@Override
	public int compare(Summary o1, Summary o2)
	{
		Language language1=o1.getLanguage();
		int priority1=language1!=null && "en".equals(language1.getSymbol()) ? 1 : 2;
		Language language2=o2.getLanguage();
		int priority2=language2!=null && "en".equals(language2.getSymbol()) ? 1 : 2;
		return priority1-priority2;
	}
}
