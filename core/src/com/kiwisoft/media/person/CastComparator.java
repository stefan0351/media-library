package com.kiwisoft.media.person;

import java.util.Comparator;

import com.kiwisoft.media.person.CastMember;

/**
 * @author Stefan Stiller
 */
public class CastComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		CastMember cast1=(CastMember)o1;
		CastMember cast2=(CastMember)o2;
		String actor1=cast1.getActor()!=null ? cast1.getActor().toString() : "";
		String actor2=cast2.getActor()!=null ? cast2.getActor().toString() : "";
		int result=actor1.compareToIgnoreCase(actor2);
		if (result==0) return cast1.getId().compareTo(cast2.getId());
		return result;
	}
}
