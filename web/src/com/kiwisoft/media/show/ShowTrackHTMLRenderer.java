package com.kiwisoft.media.show;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.Language;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.HTMLRendererManager;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 18.03.2007
 * Time: 18:58:57
 * To change this template use File | Settings | File Templates.
 */
public class ShowTrackHTMLRenderer extends DefaultHTMLRenderer
{
	public ShowTrackHTMLRenderer()
	{
	}

	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Track)
		{
			Track track=(Track)value;
			StringBuilder buffer=new StringBuilder();
			Movie movie=track.getMovie();
			context.put(Language.class.getName(), track.getLanguage());
			HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
			if (movie!=null) buffer.append(rendererManager.getRenderer(Movie.class).getContent(movie, context, rowIndex, columnIndex));
			else
			{
				Episode episode=track.getEpisode();
				if (episode!=null) buffer.append(rendererManager.getRenderer(Episode.class).getContent(episode, context, rowIndex, columnIndex));
			}
			String event=track.getEvent();
			if (event!=null)
			{
				if (buffer.length()>0) buffer.append(" - ");
				buffer.append(StringEscapeUtils.escapeHtml(event));
			}
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
