package com.kiwisoft.media.show;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.Language;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class ShowTrackHTMLRenderer extends DefaultHTMLRenderer
{
	public ShowTrackHTMLRenderer()
	{
	}

	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Track)
		{
			Track track=(Track)value;
			StringBuilder buffer=new StringBuilder();
			Movie movie=track.getMovie();
			context.setProperty(Language.class.getName(), track.getLanguage());
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
