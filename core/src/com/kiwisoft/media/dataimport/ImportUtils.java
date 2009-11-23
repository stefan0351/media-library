/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 12, 2003
 * Time: 6:51:02 PM
 */
package com.kiwisoft.media.dataimport;

import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.html.HtmlUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.File;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImportUtils
{
	private final static Log log=LogFactory.getLog(ImportUtils.class);

	public static final DateFormat DATE_FORMAT=new SimpleDateFormat("d.M.yyyy H:mm");
	public static final boolean USE_CACHE=false;

	private ImportUtils()
	{
	}

	public static String toPreformattedText(String text, boolean ignoreParagraphs)
	{
		if (text==null) return null;
		if (!ignoreParagraphs)
		{
			text=text.replaceAll("<p *>", "[br/][br/]");
			text=text.replaceAll("</p>", "");
		}
		text=replaceTags(text, "i");
		text=replaceTags(text, "u");
		text=replaceTags(text, "b");
		text=replaceTags(text, "sup");
		text=replaceTags(text, "sub");
		text=replaceTags(text, "em");
		text=replaceTags(text, "br");
		return HtmlUtils.trimUnescape(XMLUtils.removeTags(text));
	}

	public static String toPreformattedText(String text)
	{
		return toPreformattedText(text, false);
	}

	private static String replaceTags(String line, String name)
	{
		line=Pattern.compile("<"+name+" *>", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("["+name+"]");
		line=Pattern.compile("</"+name+" *>", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("[/"+name+"]");
		line=Pattern.compile("<"+name+" */>", Pattern.CASE_INSENSITIVE).matcher(line).replaceAll("["+name+"/]");
		return line;
	}

	public static String loadUrl(String url) throws IOException
	{
		return loadUrl(url, null);
	}

	public static String loadUrl(String url, String charSetName) throws IOException
	{
		if (charSetName==null) charSetName=Charset.defaultCharset().name();
		if (USE_CACHE)
		{
			File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8")+".html");
			if (file.exists())
			{
				log.debug("Loading cached data from "+file.getAbsolutePath());
				return FileUtils.loadFile(file, charSetName);
			}
		}
		int tries=0;
		while (true)
		{
			try
			{
				String page=WebUtils.loadURL(url, null, charSetName);
				if (USE_CACHE)
				{
					File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8")+".html");
					file.getParentFile().mkdirs();
					FileUtils.saveToFile(page, file, charSetName);
				}
				return page;
			}
			catch (IOException e)
			{
				tries++;
				if (tries>=3) throw e;
			}
		}
	}

	public static byte[] loadUrlBinary(String url) throws IOException
	{
		if (USE_CACHE)
		{
			File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8"));
			if (file.exists())
			{
				log.debug("Loading cached data from "+file.getAbsolutePath());
				return FileUtils.loadBinaryFile(file);
			}
		}
		int tries=0;
		while (true)
		{
			try
			{
				byte[] data=WebUtils.loadBytesFromURL(url);
				if (USE_CACHE)
				{
					File file=new File("tmp"+File.separator+"web", URLEncoder.encode(url, "UTF-8"));
					file.getParentFile().mkdirs();
					FileUtils.saveToFile(data, file);
				}
				return data;
			}
			catch (IOException e)
			{
				tries++;
				if (tries>=3) throw e;
			}
		}
	}
}
