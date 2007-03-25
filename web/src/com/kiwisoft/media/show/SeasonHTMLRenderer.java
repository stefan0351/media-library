package com.kiwisoft.media.show;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Navigation;

/**
 * @author Stefan Stiller
 */
public class SeasonHTMLRenderer extends DefaultHTMLRenderer
{
	private String linkClass;

	public SeasonHTMLRenderer()
	{
		linkClass="link";
	}

	public SeasonHTMLRenderer(String linkClass)
	{
		this.linkClass=linkClass;
	}

	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Season)
		{
			Season season=(Season)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"").append(linkClass).append("\" href=\"").append(Navigation.getLink(season)).append("\">");
			buffer.append(StringEscapeUtils.escapeHtml(season.getSeasonName()));
			String yearString=season.getYearString();
			if (yearString!=null) buffer.append(" <small>(").append(StringEscapeUtils.escapeHtml(yearString)).append(")</small>");
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
