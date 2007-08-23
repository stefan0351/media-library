/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 19, 2003
 * Time: 8:04:49 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.io.File;
import java.util.Date;
import javax.servlet.ServletContext;

import com.kiwisoft.format.FormatManager;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.web.PreformattedHTMLRenderer;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.medium.TrackHTMLRenderer;
import com.kiwisoft.media.medium.MediumHTMLRenderer;
import com.kiwisoft.media.show.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieHTMLRenderer;
import com.kiwisoft.media.schedule.AirdateHTMLRenderer;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonHTMLRenderer;
import com.kiwisoft.media.pics.Picture;
import com.kiwisoft.media.pics.PictureFormat;
import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.photos.PhotoGalleryFormat;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.app.Application;

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
		new Application("media");
		String path=context.getRealPath("WEB-INF/config.xml");
		new SimpleConfiguration().loadDefaultsFromFile(new File(path));
		Icons.setResource("/com/kiwisoft/media/icons/CoreIcons.xml");
		initializeFormats();
		initializeRenderers();
	}

	protected void initializeFormats()
	{
		FormatManager formatManager=FormatManager.getInstance();
		formatManager.setFormat(Picture.class, new PictureFormat());
		formatManager.setFormat(PhotoGallery.class, new PhotoGalleryFormat());
	}

	private void initializeRenderers()
	{
		HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
		rendererManager.setRenderer(Date.class, "Date only", new DefaultHTMLRenderer("Date only"));
		rendererManager.setRenderer(String.class, "preformatted", new PreformattedHTMLRenderer());
		rendererManager.setRenderer(Language.class, new LanguageHTMLRenderer());
		rendererManager.setRenderer(Language.class, "icon only", new LanguageHTMLRenderer(false));
		rendererManager.setRenderer(Country.class, new CountryHTMLRenderer());
		rendererManager.setRenderer(Medium.class, new MediumHTMLRenderer(null));
		rendererManager.setRenderer(Medium.class, MediumHTMLRenderer.NAME, new MediumHTMLRenderer(MediumHTMLRenderer.NAME));
		rendererManager.setRenderer(Medium.class, MediumHTMLRenderer.FULL, new MediumHTMLRenderer(MediumHTMLRenderer.FULL));
		rendererManager.setRenderer(Track.class, "Show", new ShowTrackHTMLRenderer());
		rendererManager.setRenderer(Track.class, new TrackHTMLRenderer());
		rendererManager.setRenderer(Airdate.class, new AirdateHTMLRenderer());
		rendererManager.setRenderer(Episode.class, new EpisodeHTMLRenderer());
		rendererManager.setRenderer(Movie.class, new MovieHTMLRenderer());
		rendererManager.setRenderer(Person.class, new PersonHTMLRenderer());
		rendererManager.setRenderer(Show.class, new ShowHTMLRenderer());
		rendererManager.setRenderer(Genre.class, new GenreHTMLRenderer());
		rendererManager.setRenderer(Season.class, new SeasonHTMLRenderer());
		rendererManager.setRenderer(Season.class, "Menu", new SeasonHTMLRenderer("menulink"));
	}
}

