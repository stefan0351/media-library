package com.kiwisoft.media.links;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.Navigation;

/**
 * @author Stefan Stiller
 */
public class LinkGroupHierarchyHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof LinkGroup)
		{
			StringBuilder html=new StringBuilder();
			LinkGroup linkGroup=(LinkGroup)value;
			while (linkGroup!=null)
			{
				html.insert(0, "</a>");
				html.insert(0, StringEscapeUtils.escapeHtml(linkGroup.getName()));
				html.insert(0, " &raquo; <a class=\"link\" href=\""+Navigation.getLink(context.getRequest(), linkGroup)+"\">");
				linkGroup=linkGroup.getParentGroup();
			}
			html.insert(0, "<a class=\"link\" href=\""+context.getContextPath()+"/Links.action\">Root</a>");
			return html.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}