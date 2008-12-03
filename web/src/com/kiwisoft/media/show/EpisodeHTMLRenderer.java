package com.kiwisoft.media.show;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.Navigation;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class EpisodeHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Episode)
		{
			Episode episode=(Episode)value;
			Language language=(Language)context.getProperty(Language.class.getName());
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), episode)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(episode.getTitleWithKey(language)));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
