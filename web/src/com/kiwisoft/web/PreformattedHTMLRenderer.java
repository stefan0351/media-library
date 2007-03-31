package com.kiwisoft.web;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Stefan Stiller
 */
public class PreformattedHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof String)
		{
			StringBuilder output=new StringBuilder();
			String[] lines=value.toString().split("\n");
			for (int i=0; i<lines.length; i++)
			{
				if (i>0) output.append("<br/>");
				String line=lines[i];
				line=StringEscapeUtils.escapeHtml(line);
				line=line.replace("[i]", "<i>");
				line=line.replace("[/i]", "</i>");
				line=line.replace("[b]", "<b>");
				line=line.replace("[/b]", "</b>");
				output.append(line);
			}
			return output.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
