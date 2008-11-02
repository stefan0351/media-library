package com.kiwisoft.media;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.HTMLRenderer;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class ChannelHTMLRenderer extends DefaultHTMLRenderer
{
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
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
			Picture logo=channel.getLogo();
			if (logo!=null)
			{
				HTMLRenderer pictureRenderer=HTMLRendererManager.getInstance().getRenderer(Picture.class);
				Map<String, Object> pictureContext=new HashMap<String, Object>(context);
				pictureContext.put("name", channel.getName());
				output.append(pictureRenderer.getContent(logo, pictureContext, rowIndex, columnIndex));
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
