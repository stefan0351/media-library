package com.kiwisoft.media.pics;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;

/**
 * @author Stefan Stiller
 */
public class Picture extends IDObject
{
	public static final String NAME="name";
	public static final String FILE="file";
	public static final String WIDTH="width";
	public static final String HEIGHT="height";
	public static final String VARIANT="variant";
	public static final String THUMBNAIL_50x50="thumbnail50x50";
	public static final String THUMBNAIL_SIDEBAR="thumbnailSidebar";

	private String name;
	private String file;
	private int width;
	private int height;

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

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		String oldFile=this.file;
		this.file=file;
		setModified();
		firePropertyChange(FILE, oldFile, file);
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		int oldWidth=this.width;
		this.width=width;
		setModified();
		firePropertyChange(WIDTH, oldWidth, width);
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		int oldHeight=this.height;
		this.height=height;
		setModified();
		firePropertyChange(HEIGHT, oldHeight, height);
	}

	public void setThumbnail(String property, String path, int imageWidth, int imageHeight)
	{
		Thumbnail thumbnail=(Thumbnail)getReference(property);
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
				thumbnail=new Thumbnail();
				setReference(property, thumbnail);
			}
			thumbnail.setFile(path);
			thumbnail.setWidth(imageWidth);
			thumbnail.setHeight(imageHeight);
		}
	}

	public Thumbnail getThumbnail50x50()
	{
		return (Thumbnail)getReference(THUMBNAIL_50x50);
	}

	public void setThumbnail50x50(Thumbnail thumbnail)
	{
		setReference(THUMBNAIL_50x50, thumbnail);
	}

	public void setThumbnail50x50(String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_50x50, path, imageWidth, imageHeight);
	}

	public Thumbnail getThumbnailSidebar()
	{
		return (Thumbnail)getReference(THUMBNAIL_SIDEBAR);
	}

	public void setThumbnailSidebar(Thumbnail thumbnail)
	{
		setReference(THUMBNAIL_SIDEBAR, thumbnail);
	}

	public void setThumbnailSidebar(String path, int imageWidth, int imageHeight)
	{
		setThumbnail(THUMBNAIL_SIDEBAR, path, imageWidth, imageHeight);
	}
}
