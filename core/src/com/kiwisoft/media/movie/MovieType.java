/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 16, 2003
 * Time: 11:16:36 PM
 */
package com.kiwisoft.media.movie;

import java.util.*;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.Identifyable;

public class MovieType implements Identifyable
{
	public static final Map<Long, MovieType> map=new HashMap<Long, MovieType>();

	public static final MovieType COMEDY=new MovieType(new Long(1), "Kom\u00f6die");
	public static final MovieType ROMATIC_COMEDY=new MovieType(new Long(2), "Liebeskom\u00f6die");
	public static final MovieType ACTION_COMEDY=new MovieType(new Long(3), "Actionkom\u00f6die");
	public static final MovieType TEEN_COMEDY=new MovieType(new Long(4), "Teeniekom\u00f6die");
	public static final MovieType HORROR_COMEDY=new MovieType(new Long(5), "Horrorkom\u00f6die");
	public static final MovieType EROTIC_DRAMA=new MovieType(new Long(6), "Erotikdrama");

	public static MovieType get(Long id)
	{
		return map.get(id);
	}

	public static Collection<MovieType> getAll()
	{
		return map.values();
	}

	public static Collection<MovieType> getAll(String pattern)
	{
		Set<MovieType> result=new HashSet<MovieType>();
		Iterator<MovieType> it=map.values().iterator();
		while (it.hasNext())
		{
			MovieType type=it.next();
			if (StringUtils.matchExpression(type.getName(), pattern)) result.add(type);
		}
		return result;
	}

	private Long id;
	private String name;

	private MovieType(Long id, String name)
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

	public Collection<Movie> getMovies()
	{
		return DBLoader.getInstance().loadSet(Movie.class, null, "type_id=?", getId());
	}
}
