package com.kiwisoft.media.tags;

import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFile;

import java.io.IOException;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ImageTag extends StrutsBodyTagSupport
{
	private String image;
	private String attributes;

	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			ImageFile imageFile=(ImageFile) findValue(image, ImageFile.class);
			JspWriter out=pageContext.getOut();
			out.print(renderImage(imageFile));
			return SKIP_BODY;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image=image;
	}


	public String getAttributes()
	{
		return attributes;
	}

	public void setAttributes(String attributes)
	{
		this.attributes=attributes;
	}

	public String renderImage(ImageFile imageFile)
	{
		StringBuilder html=new StringBuilder();
		html.append("<img");
		String path=((HttpServletRequest) pageContext.getRequest()).getContextPath();
		if (imageFile!=null)
		{
			html.append(" src=\"").append(path).append("/file/").append(imageFile.getFileName()).append("?type=");
			if (imageFile instanceof MediaFile) html.append("Image");
			else html.append("ImageFile");
			html.append("&id=").append(imageFile.getId()).append("\"");
			html.append(" border=\"0\"");
			if (imageFile.getWidth()>0) html.append(" width=\"").append(imageFile.getWidth()).append("\"");
			if (imageFile.getHeight()>0) html.append(" height=\"").append(imageFile.getHeight()).append("\"");
			if (attributes!=null) html.append(" ").append(attributes);
		}
		else
		{
			html.append(" src=\"").append(path).append("/file?type=Icon&name=no-photo-available\" border=\"0\"");
		}
		html.append(">");
		return html.toString();
	}
}
