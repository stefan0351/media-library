package com.kiwisoft.media.pics;

import com.kiwisoft.persistence.DBDummy;

/**
 * @author Stefan Stiller
 */
public class Picture extends PictureFile
{
	public static final String NAME="name";
	public static final String VARIANT="variant";
	public static final String THUMBNAIL_50x50="thumbnail50x50";
	public static final String THUMBNAIL_SIDEBAR="thumbnailSidebar";

	private String name;

	public Picture()
	{
	}

	public Picture(DBDummy dummy)
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
		setModified();
		firePropertyChange(NAME, oldName, name);
	}

	public void setThumbnail(String property, String path, int imageWidth, int imageHeight)
	{
		PictureFile thumbnail=(PictureFile)getReference(property);
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
				thumbnail=new PictureFile();
				setReference(property, thumbnail);
			}
			thumbnail.setFile(path);
			thumbnail.setWidth(imageWidth);
			thumbnail.setHeight(imageHeight);
		}
	}

	public PictureFile getThumbnail50x50()
	{
		return (PictureFile)getReference(THUMBNAIL_50x50);
	}

	public void setThumbnail50x50(PictureFile thumbnail)
	{
		setReference(THUMBNAIL_50x50, thumbnail);
	}

	public void setThumbnail50x50(String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_50x50, path, imageWidth, imageHeight);
	}

	public PictureFile getThumbnailSidebar()
	{
		return (PictureFile)getReference(THUMBNAIL_SIDEBAR);
	}

	public void setThumbnailSidebar(PictureFile thumbnail)
	{
		setReference(THUMBNAIL_SIDEBAR, thumbnail);
	}

	public void setThumbnailSidebar(String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_SIDEBAR, path, imageWidth, imageHeight);
	}
}
