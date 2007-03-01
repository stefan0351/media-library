package com.kiwisoft.media;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 20.05.2004
 * Time: 14:44:35
 * To change this template use File | Settings | File Templates.
 */
public class CastComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		Cast cast1=(Cast)o1;
		Cast cast2=(Cast)o2;
		String actor1=cast1.getActor()!=null ? cast1.getActor().toString() : "";
		String actor2=cast2.getActor()!=null ? cast2.getActor().toString() : "";
		int result=actor1.compareToIgnoreCase(actor2);
		if (result==0) return cast1.getId().compareTo(cast2.getId());
		return result;
	}
}
