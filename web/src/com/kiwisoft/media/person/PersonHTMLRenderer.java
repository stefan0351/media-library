package com.kiwisoft.media.person;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.Navigation;
import com.kiwisoft.web.DefaultHTMLRenderer;

/**
 * @author Stefan Stiller
 */
public class PersonHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Person)
		{
			Person person=(Person)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(person)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(person.getName()));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
