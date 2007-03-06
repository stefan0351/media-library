package com.kiwisoft.utils;

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
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
				String line=lines[i];
				line=StringEscapeUtils.escapeHtml(line);
				line=line.replace("[i]", "<i>");
				line=line.replace("[/i]", "</i>");
				line=line.replace("[b]", "<b>");
				line=line.replace("[/b]", "</b>");
				output.append(line);
			}
		}
		return output.toString();
	}

	public static String prepareDate(Date date, Locale locale)
	{
		return DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(date);
	}

	public static String prepare(Object value)
	{
		if (value!=null) return prepareString(value.toString());
		return "";
	}

	public static String prepareSet(Set set)
	{
		StringBuilder output=new StringBuilder();
		Set sortedSet=new TreeSet(StringUtils.getComparator());
		sortedSet.addAll(set);
		for (Object o : sortedSet)
		{
			if (output.length()>0) output.append(", ");
			output.append(prepare(o));
		}
		return output.toString();
	}
}
