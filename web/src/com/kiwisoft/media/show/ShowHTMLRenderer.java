package com.kiwisoft.media.show;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Navigation;

/**
 * @author Stefan Stiller
 */
public class ShowHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Show)
		{
			Show show=(Show)value;
			Language language=(Language)context.get(Language.class.getName());
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(show)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(show.getTitle(language)));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
