package com.kiwisoft.media.pics;

import java.io.File;
import java.awt.Dimension;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
*/
public class ImageData
{
	private File file;
	private Dimension size;

	public ImageData(File file, Dimension size)
	{
		this.file=file;
		this.size=size;
	}

	public File getFile()
	{
		return file;
	}

	public String getPath()
	{
		return FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath());
	}

	public Dimension getSize()
	{
		return size;
	}
}
