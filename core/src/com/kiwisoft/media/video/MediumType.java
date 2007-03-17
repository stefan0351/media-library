/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.video;

import java.util.*;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.Identifyable;

public class MediumType implements Identifyable
{
	public static final Map<Long, MediumType> map=new HashMap<Long, MediumType>();

	public static final MediumType VHS=new MediumType(new Long(2), "Video", "Videos", true);
	public static final MediumType VHS_ORIGINAL=new MediumType(new Long(1), "Video - Original", "Videos - Original");
	public static final MediumType VCD=new MediumType(new Long(3), "Video-CD", "Video-CD's");
	public static final MediumType DVD=new MediumType(new Long(4), "DVD", "DVD's");
	public static final MediumType DVD_ORIGINAL=new MediumType(new Long(5), "DVD - Original", "DVD's - Original");

	public static MediumType get(Long id)
	{
		return map.get(id);
	}

	public static Collection<MediumType> getAll()
	{
		return map.values();
	}

	public static Collection<MediumType> getAll(String pattern)
	{
		Set<MediumType> result=new HashSet<MediumType>();
		Iterator<MediumType> it=map.values().iterator();
		while (it.hasNext())
		{
			MediumType type=it.next();
			if (StringUtils.matchExpression(type.getName(), pattern)) result.add(type);
		}
		return result;
	}

	private Long id;
	private String name;
	private String pluralName;
	private boolean rewritable;

	private MediumType(Long id, String name, String pluralName)
	{
		this(id, name, pluralName, false);
	}

	private MediumType(Long id, String name, String pluralName, boolean rewritable)
	{
		this.id=id;
		this.name=name;
		this.pluralName=pluralName;
		this.rewritable=rewritable;
		map.put(id, this);
	}

	public Long getId()
	{
		return id;
	}

	public boolean isRewritable()
	{
		return rewritable;
	}

	public String getPluralName()
	{
		return pluralName;
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
