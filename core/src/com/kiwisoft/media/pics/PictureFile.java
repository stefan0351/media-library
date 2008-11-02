package com.kiwisoft.media.pics;

import java.awt.Dimension;
import java.io.File;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.persistence.DBDummy;
import com.kiwisoft.cfg.Configuration;

/**
 * @author Stefan Stiller
 */
public class PictureFile extends IDObject
{
	public static final String ROOT="root";
	public static final String FILE="file";
	public static final String WIDTH="width";
	public static final String HEIGHT="height";

	private String file;
	private String root;
	private int width;
	private int height;

	public PictureFile(String root)
	{
		setRoot(root);
	}

	public PictureFile(String root, PictureDetails imageData)
	{
		setRoot(root);
		setFile(FileUtils.getRelativePath(Configuration.getInstance().getString(root), imageData.getFile().getAbsolutePath()));
		setWidth(imageData.getWidth());
		setHeight(imageData.getHeight());
	}

	public PictureFile(String root, ImageData imageData)
	{
		setRoot(root);
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

	public String getRoot()
	{
		return root;
	}

	public void setRoot(String root)
	{
		String oldRoot=this.root;
		this.root=root;
		setModified(ROOT, oldRoot, this.root);
	}

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		String oldFile=this.file;
		this.file=file;
		setModified(FILE, oldFile, file);
	}

	public File getPhysicalFile()
	{
		String file=getFile();
		if (!StringUtils.isEmpty(file))
		{
			return FileUtils.getFile(Configuration.getInstance().getString(getRoot()), file);
		}
		return null;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		int oldWidth=this.width;
		this.width=width;
		setModified(WIDTH, oldWidth, width);
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		int oldHeight=this.height;
		this.height=height;
		setModified(HEIGHT, oldHeight, height);
	}

	public void deletePhysically()
	{
		super.delete();
		getPhysicalFile().delete();
	}
}
