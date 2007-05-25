package com.kiwisoft.media;

import com.kiwisoft.utils.Configurator;

/**
 * @author Stefan Stiller
 */
public class MediaConfiguration
{
	private MediaConfiguration()
	{
	}

	public static String getBookCoverPath()
	{
		return Configurator.getInstance().getString("path.books.covers");
	}

	public static String getPhotoThumbnailPath()
	{
		return Configurator.getInstance().getString("path.photos.thumbnails");
	}

	public static String getRootPath()
	{
		return Configurator.getInstance().getString("path.root");
	}

	public static int nextThumbnailId()
	{
		Configurator configurator=Configurator.getInstance();
		int id=configurator.getInt("thumbnail.id", 1);
		configurator.setInt("thumbnail.id", id+1);
		return id;
	}

	public static String getRecentPhotoPath()
	{
		return Configurator.getInstance().getString("path.photos.recent", null);
	}

	public static void setRecentPhotoPath(String path)
	{
		Configurator.getInstance().setString("path.photos.recent", path);
	}
}
