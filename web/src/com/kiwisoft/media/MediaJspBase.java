package com.kiwisoft.media;

import javax.servlet.http.HttpServletRequest;

import org.apache.jasper.runtime.HttpJspBase;

import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.web.JspUtils;

/**
 * @author Stefan Stiller
 */
public abstract class MediaJspBase extends HttpJspBase
{
	public String renderMedia(HttpServletRequest request, String name, MediaFile picture, ImageFile thumbnail, String otherAttributes)
	{
		name=JspUtils.render(request, name);
		String url=request.getContextPath()+"/file?type=Image&id="+picture.getId();
		int width=picture.getWidth();
		int height=picture.getHeight();
		if (width>500 || height>500)
		{
			if (width>height)
			{
				width=500;
				height=-1;
			}
			else
			{
				width=-1;
				height=500;
			}
		}
		StringBuilder html=new StringBuilder();
		html.append("<img");
		html.append(" src=\"").append(request.getContextPath()).append("/file?type=");
		if (thumbnail instanceof MediaFile) html.append("Image");
		else html.append("ImageFile");
		html.append("&id=").append(thumbnail.getId()).append("\"");
		html.append(" border=\"0\"");
		if (thumbnail.getWidth()>0) html.append(" width=\"").append(thumbnail.getWidth()).append("\"");
		if (thumbnail.getHeight()>0) html.append(" height=\"").append(thumbnail.getHeight()).append("\"");
		if (otherAttributes!=null) html.append(" ").append(otherAttributes);
		html.append(" onMouseOver=\"imagePopup('").append(name).append("', '").append(url).append("', ").append(width).append(", ").append(height)
			.append(")\"");
		html.append(" onMouseOut=\"nd()\"");
		html.append(">");
		return html.toString();
	}

	public String renderImage(HttpServletRequest request, ImageFile imageFile, String otherAttributes)
	{
		StringBuilder html=new StringBuilder();
		html.append("<img");
		html.append(" src=\"").append(request.getContextPath()).append("/file?type=");
		if (imageFile instanceof MediaFile) html.append("Image");
		else html.append("ImageFile");
		html.append("&id=").append(imageFile.getId()).append("\"");
		html.append(" border=\"0\"");
		if (imageFile.getWidth()>0) html.append(" width=\"").append(imageFile.getWidth()).append("\"");
		if (imageFile.getHeight()>0) html.append(" height=\"").append(imageFile.getHeight()).append("\"");
		if (otherAttributes!=null) html.append(" ").append(otherAttributes);
		html.append(">");
		return html.toString();
	}
}
