package com.kiwisoft.media.pics;

import java.awt.Dimension;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;

/**
 * @author Stefan Stiller
 */
public class PictureFile extends IDObject
{
	public static final String FILE="file";
	public static final String WIDTH="width";
	public static final String HEIGHT="height";

	private String file;
	private int width;
	private int height;

	public PictureFile()
	{
	}

	public PictureFile(PictureDetails imageData)
	{
		setFile(FileUtils.getRelativePath(MediaConfiguration.getRootPath(), imageData.getFile().getAbsolutePath()));
		setWidth(imageData.getWidth());
		setHeight(imageData.getHeight());
	}

	public PictureFile(ImageData imageData)
	{
		setImageData(imageData);
	}

	public void setImageData(ImageData imageData)
	{
		if (imageData!=null)
		{
			setFile(imageData.getPath());
			setSize(imageData.getSize());
		}
	}

	private void setSize(Dimension size)
	{
		if (size!=null)
		{
			setWidth(size.width);
			setHeight(size.height);
		}
	}

	public PictureFile(DBDummy dummy)
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

	public void deletePhysically()
	{
		super.delete();
		FileUtils.getFile(MediaConfiguration.getRootPath(), getFile()).delete();
	}
}
