package com.kiwisoft.media;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;

/**
 * @author Stefan Stiller
 */
public class CountryHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Country)
		{
			Country country=(Country)value;
			String icon=getIcon(country.getSymbol());
			StringBuilder output=new StringBuilder();
			if (icon!=null) output.append("<img src=\"").append(icon).append("\"> ");
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
