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
	@Override
	public String startTag(XMLContext context, String tagName, Attributes attributes)
	{
		StringBuilder builder=new StringBuilder("<"+tagName);
		for (int i=0; i<attributes.getLength(); i++)
		{
			String name=attributes.getLocalName(i);
			String value=attributes.getValue(i);
			builder.append(" ");
			builder.append(name);
			builder.append("=\"");
			if ("src".equalsIgnoreCase(name))
			{
				File file=new File(new File(context.getFileName()).getParentFile(), value);
				String contextPath=(String)context.getAttribute("contextPath");
				if (contextPath!=null)
				{
					builder.append(contextPath);
					builder.append("/");
				}
				builder.append("res/");
				builder.append(FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath()).replace('\\', '/'));
			}
			else builder.append(value);
			builder.append("\"");
		}
		builder.append("/>");
		return builder.toString();
	}

	@Override
	public String endTag(XMLContext context, String name)
	{
		return null;
	}
}
