package com.kiwisoft.media.video;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.Language;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 18.03.2007
 * Time: 18:58:57
 * To change this template use File | Settings | File Templates.
 */
public class RecordingHTMLRenderer extends DefaultHTMLRenderer
{
	public RecordingHTMLRenderer()
	{
	}

	@Override
	public String getContent(Object value, Map<String, Object> context, int rowIndex, int columnIndex)
	{
		if (value instanceof Recording)
		{
			Recording recording=(Recording)value;
			StringBuilder buffer=new StringBuilder();
			Movie movie=recording.getMovie();
			context.put(Language.class.getName(), recording.getLanguage());
			HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
			if (movie!=null) buffer.append(rendererManager.getRenderer(Movie.class).getContent(movie, context, rowIndex, columnIndex));
			else
			{
				Show show=recording.getShow();
				if (show!=null)
				{
					buffer.append(rendererManager.getRenderer(Show.class).getContent(show, context, rowIndex, columnIndex));
					Episode episode=recording.getEpisode();
					if (episode!=null)
					{
						buffer.append(" - ");
						buffer.append(rendererManager.getRenderer(Episode.class).getContent(episode, context, rowIndex, columnIndex));
					}
				}
			}
			String event=recording.getEvent();
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
