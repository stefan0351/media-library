/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.person;

import java.util.HashMap;
import java.util.Map;

import com.kiwisoft.utils.db.Identifyable;

public class Sex implements Identifyable
{
	public static final Map<Long, Sex> map=new HashMap<Long, Sex>(3);

	public static final Sex FEMALE=new Sex(1L, "Weiblich");
	public static final Sex MALE=new Sex(2L, "Männlich");
	public static final Sex UNKNOWN=new Sex(3L, "Unbekannt");

	public static Sex get(Long id)
	{
		return map.get(id);
	}

	private Long id;
	private String name;

	private Sex(Long id, String name)
	{
		this.id=id;
		this.name=name;
		map.put(id, this);
	}

	public Long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return getName();
	}
}
