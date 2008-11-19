package com.kiwisoft.media.medium;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import com.kiwisoft.utils.Identifyable;

public class TrackType implements Identifyable
{
	public static final Map<Long, TrackType> map=new HashMap<Long, TrackType>(3);

	public static final TrackType AUDIO=new TrackType(1L, "Audio");
	public static final TrackType VIDEO=new TrackType(2L, "Video");
	public static final TrackType DATA=new TrackType(3L, "Data");
	public static final TrackType SUBTITLES=new TrackType(4L, "Subtitles");

	public static TrackType valueOf(Long id)
	{
		return map.get(id);
	}

	public static Collection<TrackType> values()
	{
		return map.values();
	}

	private Long id;
	private String name;

	private TrackType(Long id, String name)
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

	public Object getPrimaryKey()
	{
		return getId();
	}
}
