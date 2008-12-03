package com.kiwisoft.media.pics;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class ImageFileHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof ImageFile)
		{
			ImageFile pictureFile=(ImageFile)value;
			StringBuilder output=new StringBuilder();
			output.append("<img");
			output.append(" src=\"").append(context.getContextPath()).append("/file/").append(pictureFile.getFileName()).append("?type=");
			if (pictureFile instanceof MediaFile) output.append("Image");
			else output.append("ImageFile");
			output.append("&id=").append(pictureFile.getId()).append("\"");
			output.append(" border=\"0\"");
			if (pictureFile.getWidth()>0) output.append(" width=\"").append(pictureFile.getWidth()).append("\"");
			if (pictureFile.getHeight()>0) output.append(" height=\"").append(pictureFile.getHeight()).append("\"");
			String name=(String)context.getProperty("name");
			if (name==null && pictureFile instanceof MediaFile)
			{
				name=((MediaFile)pictureFile).getName();
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
