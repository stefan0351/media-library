package com.kiwisoft.web;

/**
 * @author Stefan Stiller
 * @since 17.10.2009
 */
public class PlainHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof String)
		{
			String html=(String) value;
			html=html.replace("${contextPath}", context.getContextPath());
			return html;
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
