package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.Navigation;
import com.kiwisoft.media.fanfic.Author;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Iterator;

/**
 * @author Stefan Stiller
 * @since 15.10.2009
 */
public class FanFicHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof FanFic)
		{
			FanFic fanFic=(FanFic) value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), fanFic)).append("\">");
			buffer.append("&quot;");
			buffer.append(StringEscapeUtils.escapeHtml(fanFic.getTitle()));
			buffer.append("&quot;</a> by ");
			for (Iterator<Author> it=fanFic.getAuthors().iterator(); it.hasNext();)
			{
				Author author=it.next();
				buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), author)).append("\">");
				buffer.append(StringEscapeUtils.escapeHtml(author.getName()));
				buffer.append("</a>");
				if (it.hasNext()) buffer.append(", ");
			}
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
