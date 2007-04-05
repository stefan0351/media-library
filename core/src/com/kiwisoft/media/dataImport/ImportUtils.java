/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 12, 2003
 * Time: 6:51:02 PM
 */
package com.kiwisoft.media.dataImport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.kiwisoft.utils.DateUtils;

public class ImportUtils
{
	public static final DateFormat DATE_FORMAT=new SimpleDateFormat("d.M.yyyy H:mm");

	static
	{
		DATE_FORMAT.setTimeZone(DateUtils.GMT);
	}

	private ImportUtils()
	{
	}

	public static String replaceHtmlFormatTags(String text)
	{
		if (text==null) return null;
		text=replaceTags(text, "i");
		text=replaceTags(text, "u");
		text=replaceTags(text, "b");
		text=replaceTags(text, "sup");
		text=replaceTags(text, "sub");
		text=replaceTags(text, "em");
		return text;
	}


	private static String replaceTags(String line, String name)
	{
		line=line.replace("<"+name+">", "["+name+"]");
		line=line.replace("</"+name+">", "[/"+name+"]");
		return line;
	}

}
