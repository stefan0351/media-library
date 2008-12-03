package com.kiwisoft.web;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Stefan Stiller
 */
public class PreformattedHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof String)
		{
			StringBuilder output=new StringBuilder();
			String[] lines=value.toString().split("\n");
			for (int i=0; i<lines.length; i++)
			{
				String line=lines[i];
				line=StringEscapeUtils.escapeHtml(line);
				line=replaceTags(line, "br");
				line=replaceTags(line, "i");
				line=replaceTags(line, "u");
				line=replaceTags(line, "b");
				line=replaceTags(line, "sup");
				line=replaceTags(line, "sub");
				line=replaceTags(line, "em");
				output.append(line);
			}
			return output.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}

	private String replaceTags(String line, String name)
	{
		line=line.replace("["+name+"]", "<"+name+">");
		line=line.replace("[/"+name+"]", "</"+name+">");
		line=line.replace("["+name+"/]", "<"+name+"/>");
		return line;
	}
}
