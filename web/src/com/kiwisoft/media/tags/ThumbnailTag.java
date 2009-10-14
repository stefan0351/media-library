package com.kiwisoft.media.tags;

import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.kiwisoft.web.JspUtils;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.ImageFile;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ThumbnailTag extends StrutsBodyTagSupport
{
	private String title;
	private String image;
	private String thumbnail;
	private String attributes;

	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			MediaFile imageFile=(MediaFile) findValue(image, MediaFile.class);
			ImageFile thumbnailFile=(ImageFile) findValue(thumbnail, ImageFile.class);
			HttpServletRequest request=(HttpServletRequest) pageContext.getRequest();
			String name=JspUtils.render(request, findString(title));
			String url=request.getContextPath()+"/file/"+imageFile.getFileName()+"?type=Image&id="+imageFile.getId();
			int width=imageFile.getWidth();
			int height=imageFile.getHeight();
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
			html.append(" src=\"").append(request.getContextPath()).append("/file/").append(thumbnailFile.getFileName()).append("?type=");
			if (thumbnailFile instanceof MediaFile) html.append("Image");
			else html.append("ImageFile");
			html.append("&id=").append(thumbnailFile.getId()).append("\"");
			html.append(" border=\"0\"");
			if (thumbnailFile.getWidth()>0) html.append(" width=\"").append(thumbnailFile.getWidth()).append("\"");
			if (thumbnailFile.getHeight()>0) html.append(" height=\"").append(thumbnailFile.getHeight()).append("\"");
			if (attributes!=null) html.append(" ").append(attributes);
			html.append(" onMouseOver=\"imagePopup('").append(name).append("', '").append(url).append("', ").append(width).append(", ").append(height)
				.append(")\"");
			html.append(" onMouseOut=\"nd()\"");
			html.append(">");
			JspWriter out=pageContext.getOut();
			out.print(html);
			return SKIP_BODY;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image=image;
	}

	public String getThumbnail()
	{
		return thumbnail;
	}

	public void setThumbnail(String thumbnail)
	{
		this.thumbnail=thumbnail;
	}

	public String getAttributes()
	{
		return attributes;
	}

	public void setAttributes(String attributes)
	{
		this.attributes=attributes;
	}
}
