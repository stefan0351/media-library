/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.person;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import com.kiwisoft.utils.Identifyable;

public class Gender implements Identifyable
{
	public static final Map<Long, Gender> map=new HashMap<Long, Gender>(3);

	public static final Gender FEMALE=new Gender(1L, "Female");
	public static final Gender MALE=new Gender(2L, "Male");
	public static final Gender UNKNOWN=new Gender(3L, "Unknown");

	public static Gender get(Long id)
	{
		return map.get(id);
	}

	public static Collection<Gender> values()
	{
		return map.values();	
	}

	private Long id;
	private String name;

	private Gender(Long id, String name)
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
