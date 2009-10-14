package com.kiwisoft.media.books;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;
import com.kiwisoft.media.Navigation;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class BookHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Book)
		{
			Book book=(Book)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), book)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(book.getTitle()));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
