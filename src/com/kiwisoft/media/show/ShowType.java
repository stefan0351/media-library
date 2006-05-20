/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.show;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.kiwisoft.utils.db.Identifyable;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.StringUtils;

public class ShowType implements Identifyable
{
	public static final Map<Long, ShowType> map=new HashMap<Long, ShowType>();

	public static final ShowType COMEDY=new ShowType(1L, "Comedy");
	public static final ShowType MYSTERY=new ShowType(3L, "Mystery");
	public static final ShowType SCI_FI=new ShowType(4L, "SciFi");
	public static final ShowType DRAMA=new ShowType(5L, "Drama");
	public static final ShowType CARTOON=new ShowType(6L, "Cartoon");
	public static final ShowType FANTASY=new ShowType(7L, "Fantasy");
	public static final ShowType TEEN_ACTION=new ShowType(8L, "Teenie-Action");
	public static final ShowType ACTION=new ShowType(9L, "Action");
	public static final ShowType TEEN_COMEDY=new ShowType(10L, "Teenie-Comedy");
	public static final ShowType FAMILY=new ShowType(11L, "Familienserie");

	public static ShowType get(Long id)
	{
		return map.get(id);
	}

	public static Collection<ShowType> getAll()
	{
		return map.values();
	}

	public static Collection<ShowType> getAll(String pattern)
	{
		Set<ShowType> result=new HashSet<ShowType>();
		Iterator<ShowType> it=map.values().iterator();
		while (it.hasNext())
		{
			ShowType type=it.next();
			if (StringUtils.matchExpression(type.getName(), pattern)) result.add(type);
		}
		return result;
	}

	private Long id;
	private String name;

	private ShowType(long id, String name)
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

	public Collection<Show> getShows()
	{
		return DBLoader.getInstance().loadSet(Show.class, null, "type_id=?", getId());
	}
}
