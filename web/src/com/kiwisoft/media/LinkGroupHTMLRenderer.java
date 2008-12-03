package com.kiwisoft.media;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class LinkGroupHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof LinkGroup)
		{
			LinkGroup linkGroup=(LinkGroup)value;
			StringBuilder html=new StringBuilder();
			html.append("<a class=\"link\" href=\"");
			html.append(Navigation.getLink(context.getRequest(), linkGroup));
			html.append("\">");
			html.append(StringEscapeUtils.escapeHtml(linkGroup.getName()));
			html.append("</a>");
			return html.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
