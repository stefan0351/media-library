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
public class ATagHandler implements XMLTagHandler
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
			if ("href".equalsIgnoreCase(name))
			{
				if (value.startsWith("http:")) builder.append(value);
				else
				{
					File file=new File(new File(context.getFileName()).getParentFile(), value);
					HttpServletRequest request=(HttpServletRequest)context.getAttribute("request");
					builder.append(request.getContextPath());
					builder.append("/res/");
					builder.append(FileUtils.getRelativePath(MediaConfiguration.getRootPath(), file.getAbsolutePath()).replace('\\', '/'));
				}
			}
			else builder.append(value);
			builder.append("\"");
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public String endTag(XMLContext context, String name)
	{
		return "</a>";
	}
}