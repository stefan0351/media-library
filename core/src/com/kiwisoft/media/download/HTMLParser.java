package com.kiwisoft.media.download;

import java.io.*;
import java.net.URL;
import java.util.*;

import com.kiwisoft.utils.xml.XMLUtils;

public class HTMLParser implements Parser
{
	private static Map<String, Tag> tagMap;

	public void parse(File file, URL url, List<URL> contained, List<URL> linked) throws IOException
	{
		InputStream is=new FileInputStream(file);
		String tag;
		while ((tag=XMLUtils.getNextTag(is))!=null)
		{
			String tagName=XMLUtils.getTagName(tag);
			Tag tagDef=(Tag) getTagMap().get(tagName.toLowerCase());
			if (tagDef!=null)
			{
				for (String parameter : tagDef.parameters)
				{
					String value=XMLUtils.getAttribute(tag, parameter);
					if (value!=null)
					{
						URL newURL=getURL(url, value);
						if (newURL!=null && !ExcludeFilter.getInstance().match(newURL.toString()))
						{
							if (tagDef.isLink)
							{
								if (linked!=null) linked.add(newURL);
							}
							else
							{
								if (contained!=null) contained.add(newURL);
							}
						}
					}
				}
			}
		}
		is.close();
	}

	private URL getURL(URL url, String path)
	{
		try
		{
			URL newURL=new URL(url, path);
			if ("http".equalsIgnoreCase(newURL.getProtocol()))
				return newURL;
			else
				return null;
		}
		catch (Exception e)
		{
//			System.out.println(e.getMessage());
			return null;
		}
	}

	private synchronized Map getTagMap() throws IOException
	{
		if (tagMap==null)
		{
			tagMap=new HashMap<String, Tag>();

			Properties properties=new Properties();
			properties.load(getClass().getResourceAsStream("tags.properties"));
			String tags=properties.getProperty("ELEMENTS");
			if (tags!=null)
			{
				StringTokenizer tokens=new StringTokenizer(tags, ",");
				while (tokens.hasMoreTokens())
				{
					String tagName=tokens.nextToken().trim();
					String tagParams=properties.getProperty(tagName);
					Tag tag=new Tag(tagName, tagParams, false);
					tagMap.put(tag.name, tag);
				}
			}
			tags=properties.getProperty("LINKS");
			if (tags!=null)
			{
				StringTokenizer tokens=new StringTokenizer(tags, ",");
				while (tokens.hasMoreTokens())
				{
					String tagName=tokens.nextToken().trim();
					String tagParams=properties.getProperty(tagName);
					Tag tag=new Tag(tagName, tagParams, true);
					tagMap.put(tag.name, tag);
				}
			}
		}
		return tagMap;
	}

	private static class Tag
	{
		public String name;
		public Set<String> parameters;
		public boolean isLink;

		public Tag(String aName, String aParameters, boolean link)
		{
			name=aName.toLowerCase();
			parameters=new HashSet<String>();
			StringTokenizer tokens=new StringTokenizer(aParameters, ",");
			while (tokens.hasMoreTokens()) parameters.add(tokens.nextToken().trim().toLowerCase());
			isLink=link;
		}
	}

}
