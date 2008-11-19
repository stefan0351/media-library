package com.kiwisoft.media.files;

import java.io.File;
import java.awt.Dimension;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
*/
public class ImageFileInfo
{
	private File file;
	private Dimension size;

	public ImageFileInfo(File file, Dimension size)
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
