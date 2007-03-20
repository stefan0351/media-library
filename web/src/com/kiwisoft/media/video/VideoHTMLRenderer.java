package com.kiwisoft.media.video;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.Navigation;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 18.03.2007
 * Time: 18:52:16
 * To change this template use File | Settings | File Templates.
 */
public class VideoHTMLRenderer extends DefaultHTMLRenderer
{
	private boolean showName;

	public VideoHTMLRenderer(boolean showName)
	{
		this.showName=showName;
	}

	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Video)
		{
			Video video=(Video)value;
			StringBuilder buffer=new StringBuilder();
			buffer.append("<a class=\"link\" href=\"").append(Navigation.getLink(video)).append("\">");
			if (showName || video.getUserKey()==null) buffer.append(StringEscapeUtils.escapeHtml(video.getName()));
			else buffer.append(StringEscapeUtils.escapeHtml(video.getUserKey()));
			buffer.append("</a>");
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
