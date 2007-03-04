package com.kiwisoft.media;

import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 14:41:38
 * To change this template use File | Settings | File Templates.
 */
public class Resources
{
	private Resources()
	{
	}

	public static String getResource(String name, Locale locale)
	{
		try
		{
			return ResourceBundle.getBundle(Resources.class.getName(), locale).getString(name);
		}
		catch (Exception e)
		{
			return name;
		}
	}
}
