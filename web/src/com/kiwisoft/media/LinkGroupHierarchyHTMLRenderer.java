package com.kiwisoft.media;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;

/**
 * @author Stefan Stiller
 */
public class LinkGroupHierarchyHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof LinkGroup)
		{
			StringBuilder html=new StringBuilder();
			LinkGroup linkGroup=(LinkGroup)value;
			while (linkGroup!=null)
			{
				html.insert(0, "</a>");
				html.insert(0, StringEscapeUtils.escapeHtml(linkGroup.getName()));
				html.insert(0, " &raquo; <a class=\"link\" href=\"/links.jsp?group="+linkGroup.getId()+"\">");
				linkGroup=linkGroup.getParentGroup();
			}
			html.insert(0, "<a class=\"link\" href=\"/links.jsp\">Root</a>");
			return html.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
