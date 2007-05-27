package com.kiwisoft.media;

import javax.servlet.ServletException;

import org.apache.jasper.runtime.HttpJspBase;

import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.web.JspUtils;

/**
 * @author Stefan Stiller
 */
public abstract class MediaJspBase extends HttpJspBase
{
	public void init() throws ServletException
	{
		super.init();
		MediaManagerApp.getInstance(getServletContext());
	}

	public String renderPicture(String name, Picture picture, PictureFile thumbnail, String otherAttributes)
	{
		name=JspUtils.render(name);
		String url="/"+picture.getFile().replace('\\', '/');
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
		html.append(" src=\"/").append(thumbnail.getFile().replace('\\', '/')).append("\"");
		html.append(" border=\"0\"");
		if (thumbnail.getWidth()>0) html.append(" width=\"").append(thumbnail.getWidth()).append("\"");
		if (thumbnail.getHeight()>0) html.append(" height=\"").append(thumbnail.getHeight()).append("\"");
		if (otherAttributes!=null) html.append(" ").append(otherAttributes);
		html.append(" onMouseOver=\"imagePopup('").append(name).append("', '").append(url).append("', ").append(width).append(", ").append(height).append(")\"");
		html.append(" onMouseOut=\"nd()\"");
		html.append(">");
		return html.toString();
	}

	public String renderPicture(PictureFile picture, String otherAttributes)
	{
		StringBuilder html=new StringBuilder();
		html.append("<img");
		html.append(" src=\"/").append(picture.getFile().replace('\\', '/')).append("\"");
		html.append(" border=\"0\"");
		if (picture.getWidth()>0) html.append(" width=\"").append(picture.getWidth()).append("\"");
		if (picture.getHeight()>0) html.append(" height=\"").append(picture.getHeight()).append("\"");
		if (otherAttributes!=null) html.append(" ").append(otherAttributes);
		html.append(">");
		return html.toString();
	}
}
