package com.kiwisoft.media;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;

/**
 * @author Stefan Stiller
 */
public class LanguageHTMLRenderer extends DefaultHTMLRenderer
{
	private boolean withText;

	public LanguageHTMLRenderer()
	{
		this(true);
	}

	public LanguageHTMLRenderer(boolean withText)
	{
		this.withText=withText;
	}

	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Language)
		{
			Language language=(Language)value;
            String iconName="language."+language.getSymbol().toLowerCase();
            Icon icon=Icons.getIcon(iconName);
            StringBuilder output=new StringBuilder();
			if (icon!=null) output.append("<img src=\"").append(context.getContextPath()).append("/file?type=Icon&name=").append(iconName).append("\"> ");
			if (withText || icon==null) output.append(StringEscapeUtils.escapeHtml(language.getName()));
			return output.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}

	public static String getIcon(String isoCode)
	{
		try
		{
			return ResourceBundle.getBundle(LanguageHTMLRenderer.class.getName()).getString("language."+isoCode);
		}
		catch (MissingResourceException e)
		{
			return null;
		}
	}
}
