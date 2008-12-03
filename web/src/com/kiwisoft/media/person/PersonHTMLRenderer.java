package com.kiwisoft.media.person;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.Navigation;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class PersonHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Person)
		{
			Person person=(Person)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), person)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(person.getName()));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
