package com.kiwisoft.media.files;

import com.kiwisoft.persistence.DBDummy;

/**
 * @author Stefan Stiller
 * @todo add support for audio files
 * @todo allow assignment to shows, episodes, movies and persons
 */
public class MediaFile extends ImageFile
{
	public static final int IMAGE=1;
	public static final int AUDIO=2;
	public static final int VIDEO=3;

	public static final String NAME="name";
	public static final String THUMBNAIL_50x50="thumbnail50x50";
	public static final String THUMBNAIL_SIDEBAR="thumbnailSidebar";
	public static final String MEDIA_TYPE="mediaType";
	public static final String DESCRIPTION="description";

	private String name;
	private String description;
	private int mediaType;

	public MediaFile(int mediaType, String root)
	{
		super(root);
		setMediaType(mediaType);
	}

	public MediaFile(DBDummy dummy)
	{
		super(dummy);
	}


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		String oldName=this.name;
		this.name=name;
		setModified(NAME, oldName, name);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		String oldDescription=this.description;
		this.description=description;
		setModified(DESCRIPTION, oldDescription, description);
	}

	public int getMediaType()
	{
		return mediaType;
	}

	public void setMediaType(int mediaType)
	{
		int oldMediaType=this.mediaType;
		this.mediaType=mediaType;
		setModified(MEDIA_TYPE, oldMediaType, mediaType);
	}

	public void setThumbnail(String property, String root, String path, int imageWidth, int imageHeight)
	{
		ImageFile thumbnail=(ImageFile)getReference(property);
		if (path==null)
		{
			if (thumbnail!=null)
			{
				setReference(property, null);
				thumbnail.delete();
			}
		}
		else
		{
			if (thumbnail==null)
			{
				thumbnail=new ImageFile(root);
				setReference(property, thumbnail);
			}
			thumbnail.setFile(path);
			thumbnail.setWidth(imageWidth);
			thumbnail.setHeight(imageHeight);
		}
	}

	public ImageFile getThumbnail50x50()
	{
		return (ImageFile)getReference(THUMBNAIL_50x50);
	}

	public void setThumbnail50x50(ImageFile thumbnail)
	{
		setReference(THUMBNAIL_50x50, thumbnail);
	}

	public void setThumbnail50x50(String root, String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_50x50, root, path, imageWidth, imageHeight);
	}

	public ImageFile getThumbnailSidebar()
	{
		return (ImageFile)getReference(THUMBNAIL_SIDEBAR);
	}

	public void setThumbnailSidebar(ImageFile thumbnail)
	{
		setReference(THUMBNAIL_SIDEBAR, thumbnail);
	}

	public void setThumbnailSidebar(String root, String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_SIDEBAR, root, path, imageWidth, imageHeight);
	}
}
