package com.kiwisoft.media.show;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.Genre;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class GenreHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Genre)
		{
			Genre genre=(Genre)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"");
			buffer.append(context.getContextPath());
			buffer.append("/shows/genre.jsp?genre=");
			buffer.append(genre.getId());
			buffer.append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(genre.getName()));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
