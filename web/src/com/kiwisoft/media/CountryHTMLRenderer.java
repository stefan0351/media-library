package com.kiwisoft.media;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class CountryHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Country)
		{
			Country country=(Country)value;
			String icon=getIcon(country.getSymbol());
			StringBuilder output=new StringBuilder();
			if (icon!=null) output.append("<img src=\"").append(context.getContextPath()).append("/resource?file=").append(icon).append("\"> ");
			output.append(StringEscapeUtils.escapeHtml(country.getName()));
			return output.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}

	public static String getIcon(String isoCode)
	{
		try
		{
			return ResourceBundle.getBundle(CountryHTMLRenderer.class.getName()).getString("country."+isoCode.toLowerCase());
		}
		catch (MissingResourceException e)
		{
			return null;
		}
	}
}
