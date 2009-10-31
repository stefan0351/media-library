package com.kiwisoft.media.download;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.util.ParserException;

import com.kiwisoft.utils.StringUtils;

public class HTMLParser implements Parser
{
	private static Map<String, TagProperties> tagMap;

	@Override
	public void parse(File file, final URL url, final List<URL> contained, final List<URL> linked) throws IOException
	{
		final Map<String, TagProperties> tagMap=getTagMap();
		try
		{
			org.htmlparser.Parser parser=new org.htmlparser.Parser();
			parser.setResource(file.toURI().toString());
			parser.visitAllNodesWith(new NodeVisitor()
			{
				@Override
				public void visitTag(org.htmlparser.Tag tag)
				{
					TagProperties tagProperties=tagMap.get(tag.getTagName().toLowerCase());
					if (tagProperties!=null)
					{
						for (String attribute : tagProperties.attributes)
						{
							String value=tag.getAttribute(attribute);
							if (!StringUtils.isEmpty(value))
							{
								URL newURL=GrabberUtils.getRelativeURL(url, value);
								if (newURL!=null && !ExcludeFilter.getInstance().match(newURL.toString()))
								{
									if (tagProperties.isLink)
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
			});
		}
		catch (ParserException e)
		{
			e.printStackTrace();
		}
	}

	private synchronized Map<String, TagProperties> getTagMap() throws IOException
	{
		if (tagMap==null)
		{
			tagMap=new HashMap<String, TagProperties>();

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
					TagProperties tag=new TagProperties(tagName, tagParams, false);
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
					TagProperties tag=new TagProperties(tagName, tagParams, true);
					tagMap.put(tag.name, tag);
				}
			}
		}
		return tagMap;
	}

	private static class TagProperties
	{
		public String name;
		public Set<String> attributes;
		public boolean isLink;

		public TagProperties(String aName, String aParameters, boolean link)
		{
			name=aName.toLowerCase();
			attributes=new HashSet<String>();
			StringTokenizer tokens=new StringTokenizer(aParameters, ",");
			while (tokens.hasMoreTokens()) attributes.add(tokens.nextToken().trim().toLowerCase());
			isLink=link;
		}
	}

}
