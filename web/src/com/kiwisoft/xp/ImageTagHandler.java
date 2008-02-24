package com.kiwisoft.xp;

import java.io.File;
import javax.servlet.http.HttpServletRequest;

import org.xml.sax.Attributes;

import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLTagHandler;

/**
 * @author Stefan Stiller
 */
public class ImageTagHandler implements XMLTagHandler
{
	public String startTag(XMLContext context, String uri, String localName, String rawName, Attributes attributes)
	{
		StringBuilder builder=new StringBuilder("<"+rawName);
		for (int i=0; i<attributes.getLength(); i++)
		{
			String name=attributes.getLocalName(i);
			String value=attributes.getValue(i);
			if ("src".equals(name))
			{
				File file=new File(new File(context.getFileName()).getParentFile(), value);
				HttpServletRequest request=(HttpServletRequest)context.getAttribute("request");
				System.out.println("src = "+file.getAbsolutePath());
				builder.append(" ");
				builder.append(name);
				builder.append("=\"");
				builder.append(request.getContextPath());
				builder.append("/resource?file=");
				builder.append(FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath()));
				builder.append("\"");
			}
			else builder.append(" ").append(name).append("=\"").append(value).append("\"");
		}
		builder.append("/>");
		return builder.toString();
	}

	public String endTag(XMLContext context, String uri, String localName, String rawName)
	{
		return null;
	}
}
