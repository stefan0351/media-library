package com.kiwisoft.media.files;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import com.kiwisoft.utils.Identifyable;

public class MediaType implements Identifyable
{
	public static final Map<Long, MediaType> map=new HashMap<Long, MediaType>(3);

	public static final MediaType IMAGE=new MediaType(1L, "Image", "Images");
	public static final MediaType AUDIO=new MediaType(2L, "Sound", "Sounds");
	public static final MediaType VIDEO=new MediaType(3L, "Video", "Videos");

	public static MediaType valueOf(Long id)
	{
		return map.get(id);
	}

	public static Collection<MediaType> values()
	{
		return map.values();
	}

	private Long id;
	private String name;
	private String pluralName;

	private MediaType(Long id, String name, String pluralName)
	{
		this.id=id;
		this.name=name;
		this.pluralName=pluralName;
		map.put(id, this);
	}

	public Object getPrimaryKey()
	{
		return getId();
	}

	public Long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getPluralName()
	{
		return pluralName;
	}

	public String toString()
	{
		return getName();
	}
}
