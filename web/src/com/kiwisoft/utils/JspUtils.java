package com.kiwisoft.utils;

import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 14:16:27
 * To change this template use File | Settings | File Templates.
 */
public class JspUtils
{
	private JspUtils()
	{
	}

	public static String prepareString(String value)
	{
		StringBuilder output=new StringBuilder();
		if (value!=null)
		{
			String[] lines=value.split("\n");
			for (int i=0; i<lines.length; i++)
			{
				if (i>0) output.append("<br/>");
				output.append(StringEscapeUtils.escapeHtml(lines[i]));
			}
		}
		return output.toString();
	}

	public static String prepareDate(Date date, Locale locale)
	{
		return DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(date);
	}
}
