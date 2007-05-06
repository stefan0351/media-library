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

	public static String getRootPath()
	{
		return Configurator.getInstance().getString("path.root");
	}
}
