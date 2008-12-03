package com.kiwisoft.media.schedule;

import org.apache.commons.lang.StringEscapeUtils;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.web.WebContext;

/**
 * @author Stefan Stiller
 */
public class AirdateHTMLRenderer extends DefaultHTMLRenderer
{
	public AirdateHTMLRenderer()
	{
	}

	@Override
	public String getContent(Object value, WebContext context, int rowIndex, int columnIndex)
	{
		if (value instanceof Airdate)
		{
			Airdate airdate=(Airdate)value;
			StringBuilder buffer=new StringBuilder();
			Movie movie=airdate.getMovie();
			context.setProperty(Language.class.getName(), airdate.getLanguage());
			HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
			if (movie!=null) buffer.append(rendererManager.getRenderer(Movie.class).getContent(movie, context, rowIndex, columnIndex));
			else
			{
				Show show=airdate.getShow();
				if (show!=null)
				{
					Object contextShow=context.getProperty(Show.class.getName());
					Episode episode=airdate.getEpisode();
					if (show!=contextShow)
					{
						buffer.append(rendererManager.getRenderer(Show.class).getContent(show, context, rowIndex, columnIndex));
						if (episode!=null) buffer.append(" - ");
					}
					if (episode!=null)
					{
						buffer.append(rendererManager.getRenderer(Episode.class).getContent(episode, context, rowIndex, columnIndex));
					}
				}
			}
			String event=airdate.getEvent();
			if (event!=null)
			{
				if (buffer.length()>0) buffer.append(" - ");
				buffer.append(StringEscapeUtils.escapeHtml(event));
			}
			if (!StringUtils.isEmpty(airdate.getDetailsLink()))
			{
				buffer.append(" ");
//				buffer.append(" <a target=\"_new\" href=\"");
//				buffer.append(airdate.getDetailsLink());
//				buffer.append("\">");
				buffer.append("<img");
				buffer.append(" src=\"").append(context.getContextPath()).append("/file?type=Icon&name=details\"");
				buffer.append(" onClick=\"newWindow('Details', '").append(airdate.getDetailsLink()).append("', 500, 500);\"");
				buffer.append(" alt=\"Details\"");
				buffer.append(" border=\"0\">");
//				buffer.append("</a>");
			}
			return buffer.toString();
		}
		return super.getContent(value, context, rowIndex, columnIndex);
	}
}
