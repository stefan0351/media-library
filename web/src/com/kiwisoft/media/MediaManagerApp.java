/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 19, 2003
 * Time: 8:04:49 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.io.File;
import javax.servlet.ServletContext;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.video.RecordingHTMLRenderer;
import com.kiwisoft.media.video.VideoHTMLRenderer;
import com.kiwisoft.media.show.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieHTMLRenderer;
import com.kiwisoft.media.schedule.AirdateHTMLRenderer;

public class MediaManagerApp
{
	private static MediaManagerApp instance;

	public synchronized static MediaManagerApp getInstance(ServletContext context)
	{
		if (instance==null) instance=new MediaManagerApp(context);
		return instance;
	}

	private MediaManagerApp(ServletContext context)
	{
		String path=context.getRealPath("WEB-INF/config.xml");
		Configurator.getInstance().loadDefaultsFromFile(new File(path));

		HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
		rendererManager.setRenderer(Language.class, new LanguageHTMLRenderer());
		rendererManager.setRenderer(Video.class, new VideoHTMLRenderer(false));
		rendererManager.setRenderer(Video.class, "Name", new VideoHTMLRenderer(true));
		rendererManager.setRenderer(Recording.class, "Show", new ShowRecordingHTMLRenderer());
		rendererManager.setRenderer(Recording.class, new RecordingHTMLRenderer());
		rendererManager.setRenderer(Airdate.class, new AirdateHTMLRenderer());
		rendererManager.setRenderer(Episode.class, new EpisodeHTMLRenderer());
		rendererManager.setRenderer(Movie.class, new MovieHTMLRenderer());
		rendererManager.setRenderer(Show.class, new ShowHTMLRenderer());
	}
}

