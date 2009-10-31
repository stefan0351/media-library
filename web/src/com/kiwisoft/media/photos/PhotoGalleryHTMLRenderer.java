package com.kiwisoft.media.photos;

import com.kiwisoft.media.Navigation;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Stefan Stiller
 * @since 15.10.2009
 */
public class PhotoGalleryHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof PhotoGallery)
		{
			PhotoGallery gallery=(PhotoGallery) value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(context.getRequest(), gallery)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(gallery.getName()));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
