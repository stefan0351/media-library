package com.kiwisoft.media.medium;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.Navigation;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class MediumHTMLRenderer extends DefaultHTMLRenderer
{
	public static final String NAME="Name";
	public static final String FULL="Full";

	private String variant;

	public MediumHTMLRenderer(String variant)
	{
		this.variant=variant;
	}

	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Medium)
		{
			Medium video=(Medium)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(video)).append("\">");
			String key=video.getFullKey();
			if (FULL.equals(variant))
			{
				if (!StringUtils.isEmpty(video.getName())) buffer.append(StringEscapeUtils.escapeHtml(video.getName()));
				if (!StringUtils.isEmpty(key))
				{
					buffer.append(" (");
					buffer.append(StringEscapeUtils.escapeHtml(key));
					buffer.append(")");
				}
			}
			else
			{
				if (NAME.equals(variant) || key==null) buffer.append(StringEscapeUtils.escapeHtml(video.getName()));
				else buffer.append(StringEscapeUtils.escapeHtml(key));
			}
			buffer.append("</a>");
			if (FULL.equals(variant))
			{
				if (!StringUtils.isEmpty(video.getStorage()))
				{
					buffer.append(" @ ");
					buffer.append(video.getStorage());
				}
			}
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
