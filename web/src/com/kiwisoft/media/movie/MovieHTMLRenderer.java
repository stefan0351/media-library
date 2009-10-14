package com.kiwisoft.media.movie;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.Navigation;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class MovieHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Movie)
		{
			Movie movie=(Movie)value;
			Language language=(Language)context.getProperty(Language.class.getName());
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), movie)).append("\">");
			buffer.append("&quot;").append(StringEscapeUtils.escapeHtml(movie.getTitle(language))).append("&quot;");
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
