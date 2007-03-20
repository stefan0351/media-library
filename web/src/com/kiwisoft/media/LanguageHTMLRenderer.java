package com.kiwisoft.media;

import java.util.Map;

import com.kiwisoft.web.DefaultHTMLRenderer;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 18.03.2007
 * Time: 18:22:12
 * To change this template use File | Settings | File Templates.
 */
public class LanguageHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Language)
		{
			Language language=(Language)value;
			return "<img src=\"/clipart/flag_"+language.getSymbol()+".gif\"> "+language.getName();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
