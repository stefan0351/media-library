package com.kiwisoft.media.show;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Navigation;
import com.kiwisoft.media.Genre;

/**
 * @author Stefan Stiller
 */
public class GenreHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Genre)
		{
			Genre genre=(Genre)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"");
			buffer.append(((HttpServletRequest)context.get("request")).getContextPath());
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
