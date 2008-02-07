package com.kiwisoft.media.movie;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Navigation;

/**
 * @author Stefan Stiller
 */
public class MovieHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Movie)
		{
			Movie movie=(Movie)value;
			Language language=(Language)context.get(Language.class.getName());
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(movie)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(movie.getTitle(language)));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
