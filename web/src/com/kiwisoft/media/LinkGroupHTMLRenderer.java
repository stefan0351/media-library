package com.kiwisoft.media;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;

/**
 * @author Stefan Stiller
 */
public class LinkGroupHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof LinkGroup)
		{
			LinkGroup linkGroup=(LinkGroup)value;
			StringBuilder html=new StringBuilder();
			html.append("<a class=\"link\" href=\"/links.jsp?group=").append(linkGroup.getId()).append("\">");
			html.append(StringEscapeUtils.escapeHtml(linkGroup.getName()));
			html.append("</a>");
			return html.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
