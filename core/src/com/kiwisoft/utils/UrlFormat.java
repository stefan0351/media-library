package com.kiwisoft.utils;

import java.net.MalformedURLException;
import java.net.URL;

import com.kiwisoft.format.DefaultObjectFormat;

/**
 * @author Stefan Stiller
 */
public class UrlFormat extends DefaultObjectFormat
{
	@Override
	public boolean canParse(Class aClass)
	{
		if (URL.class.equals(aClass)) return true;
		return super.canParse(aClass);
	}

	@Override
	public Object parse(String value, Class targetClass)
	{
		if (URL.class.equals(targetClass))
		{
			try
			{
				return new URL(value);
			}
			catch (MalformedURLException e)
			{
				return null;
			}
		}
		return super.parse(value, targetClass);
	}
}
