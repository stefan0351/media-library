package com.kiwisoft.media;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.HTMLRenderer;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class ChannelHTMLRenderer extends DefaultHTMLRenderer
{
	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Channel)
		{
			Channel channel=(Channel)value;
			StringBuilder output=new StringBuilder();
			String webAddress=channel.getWebAddress();
			if (!StringUtils.isEmpty(webAddress))
			{
				output.append("<a target=\"_new\" class=\"link\" href=\"").append(webAddress).append("\">");
			}
			MediaFile logo=channel.getLogo();
			if (logo!=null)
			{
				HTMLRenderer pictureRenderer=HTMLRendererManager.getInstance().getRenderer(MediaFile.class);
				context.setProperty("name", channel.getName());
				output.append(pictureRenderer.getContent(logo, context, rowIndex, columnIndex));
			}
			else
			{
				output.append(StringEscapeUtils.escapeHtml(channel.getName()));
			}
			if (!StringUtils.isEmpty(webAddress))
			{
				output.append("</a>");
			}
			return output.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
