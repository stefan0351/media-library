/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.dataImport;

import java.util.HashMap;
import java.util.Map;

import com.kiwisoft.utils.Identifyable;

public class DataSource implements Identifyable
{
	public static final Map<Object, DataSource> map=new HashMap<Object, DataSource>();

	public static final DataSource PRISMA_ONLINE=new DataSource(new Long(1), "prisma", "PrismaOnline.de");
	public static final DataSource PRO7=new DataSource(new Long(2), "pro7", "Pro7.de");
	public static final DataSource TVTV=new DataSource(new Long(3), "tvtv", "TVTV.de");
	public static final DataSource INPUT=new DataSource(new Long(4), "input", "Input");

	public static DataSource get(Long id)
	{
		return map.get(id);
	}

	public static DataSource get(String key)
	{
		return map.get(key);
	}

	private Long id;
	private String key;
	private String name;

	private DataSource(Long id, String key, String name)
	{
		this.id=id;
		this.key=key;
		this.name=name;
		map.put(id, this);
		map.put(key, this);
	}

	public Object getPrimaryKey()
	{
		return getId();
	}

	public Long getId()
	{
		return id;
	}

	public String getKey()
	{
		return key;
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
