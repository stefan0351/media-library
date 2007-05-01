package com.kiwisoft.media.pics;

import com.kiwisoft.utils.db.DBDummy;
import com.kiwisoft.utils.db.IDObject;

/**
 * @author Stefan Stiller
 */
public class Thumbnail extends IDObject
{
	public static final String FILE="file";
	public static final String WIDTH="width";
	public static final String HEIGHT="height";

	private String file;
	private int width;
	private int height;

	public Thumbnail()
	{
	}

	public Thumbnail(DBDummy dummy)
	{
		super(dummy);
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
}
