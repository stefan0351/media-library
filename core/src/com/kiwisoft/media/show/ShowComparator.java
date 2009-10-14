package com.kiwisoft.media.show;

import com.kiwisoft.utils.Utils;

import java.util.Comparator;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ShowComparator implements Comparator<Show>
{
	public int compare(Show show1, Show show2)
	{
		int result=Utils.compareNullSafe(show1.getIndexBy(), show2.getIndexBy(), false);
		if (result==0) result=show1.getId().compareTo(show2.getId());
		return result;
	}
}
