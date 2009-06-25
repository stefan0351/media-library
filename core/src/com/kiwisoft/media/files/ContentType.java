package com.kiwisoft.media.files;

import java.util.*;

import com.kiwisoft.utils.Identifyable;

public class ContentType implements Identifyable
{
	public static final Map<Long, ContentType> map=new HashMap<Long, ContentType>(3);

	public static final ContentType WALLPAPER=new ContentType(101L, "Wallpaper", "Wallpapers", MediaType.IMAGE);
	public static final ContentType SCREEN_CAPTURE=new ContentType(102L, "Screen Capture", "Screen Captures", MediaType.IMAGE);
	public static final ContentType PROMOTION_PHOTO=new ContentType(103L, "Promotion", "Promotions", MediaType.IMAGE);
	public static final ContentType POSTER=new ContentType(104L, "Poster", "Posters" , MediaType.IMAGE);
	public static final ContentType LOGO=new ContentType(105L, "Logo", "Logos", MediaType.IMAGE);
	public static final ContentType PHOTO=new ContentType(106L, "Photo", "Photos", MediaType.IMAGE);
	public static final ContentType COVER=new ContentType(107L, "Cover", "Covers", MediaType.IMAGE);

	public static final ContentType SONG=new ContentType(201L, "Song", "Songs", MediaType.AUDIO);
	public static final ContentType THEME=new ContentType(202L, "Theme", "Themes", MediaType.AUDIO);

	public static final ContentType FULL_EPISODE=new ContentType(301L, "Full Episode", "Full Episodes", MediaType.VIDEO);
	public static final ContentType PROMOTION_VIDEO=new ContentType(302L, "Promotion", "Promotions", MediaType.VIDEO);
	public static final ContentType TRAILER=new ContentType(303L, "Trailer", "Trailers", MediaType.VIDEO);
	public static final ContentType CLIP=new ContentType(304L, "Clip", "Clips", MediaType.VIDEO);
	public static final ContentType MUSIC_VIDEO=new ContentType(305L, "Music Video", "Music Videos", MediaType.VIDEO);

	public static ContentType valueOf(Long id)
	{
		return map.get(id);
	}

	public static Collection<ContentType> values()
	{
		return map.values();
	}

	public static Collection<ContentType> values(MediaType mediaType)
	{
		Set<ContentType> contentTypes=new HashSet<ContentType>();
		for (ContentType contentType : map.values())
		{
			if (mediaType==contentType.getMediaType()) contentTypes.add(contentType);
		}
		return contentTypes;
	}

	private Long id;
	private String name;
	private String pluralName;
	private MediaType mediaType;

	private ContentType(Long id, String name, String pluralName, MediaType mediaType)
	{
		this.id=id;
		this.name=name;
		this.pluralName=pluralName;
		this.mediaType=mediaType;
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

	public MediaType getMediaType()
	{
		return mediaType;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
