package com.kiwisoft.media.pics;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;

/**
 * @author Stefan Stiller
 */
public class PictureFileHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof PictureFile)
		{
			PictureFile pictureFile=(PictureFile)value;
			StringBuilder output=new StringBuilder();
			output.append("<img");
			output.append(" src=\"").append(context.get("contextPath")).append("/picture?type=");
			if (pictureFile instanceof Picture) output.append("Picture");
			else output.append("PictureFile");
			output.append("&id=").append(pictureFile.getId()).append("\"");
			output.append(" border=\"0\"");
			if (pictureFile.getWidth()>0) output.append(" width=\"").append(pictureFile.getWidth()).append("\"");
			if (pictureFile.getHeight()>0) output.append(" height=\"").append(pictureFile.getHeight()).append("\"");
			String name=(String)context.get("name");
			if (name==null && pictureFile instanceof Picture)
			{
				name=((Picture)pictureFile).getName();
			}
			if (!StringUtils.isEmpty(name))
			{
				output.append(" alt=\"").append(StringEscapeUtils.escapeHtml(name)).append("\"");
				output.append(" title=\"").append(StringEscapeUtils.escapeHtml(name)).append("\"");
			}
			output.append(">");
			return output.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
