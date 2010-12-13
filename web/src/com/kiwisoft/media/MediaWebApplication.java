/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Aug 19, 2003
 * Time: 8:04:49 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.io.IOException;
import java.io.File;
import java.util.Date;

import com.kiwisoft.web.HTMLRendererManager;
import com.kiwisoft.web.PreformattedHTMLRenderer;
import com.kiwisoft.web.DefaultHTMLRenderer;
import com.kiwisoft.web.PlainHTMLRenderer;
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
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.pics.ImageFileHTMLRenderer;
import com.kiwisoft.media.links.LinkGroupHierarchyHTMLRenderer;
import com.kiwisoft.media.links.LinkGroupHTMLRenderer;
import com.kiwisoft.media.books.Book;
import com.kiwisoft.media.books.BookHTMLRenderer;
import com.kiwisoft.media.photos.PhotoGalleryHTMLRenderer;
import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.fanfic.FanFic;
import com.kiwisoft.media.fanfics.FanFicHTMLRenderer;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.app.Application;
import com.kiwisoft.swing.icons.Icons;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Stefan Stiller
 */
public class MediaWebApplication extends MediaApplication
{
	private final static Log log=LogFactory.getLog(MediaWebApplication.class);

	public synchronized static void checkInstance()
	{
		if (!Application.hasInstance())
		{
			Application application=new MediaWebApplication();
			try
			{
				application.configureXML();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			application.initialize();
		}
	}

	@Override
	public void configureXML() throws IOException
	{
		Icons.setResource("/com/kiwisoft/media/icons/CoreIcons.xml");
		String root=System.getProperty("catalina.home");
		SimpleConfiguration configuration=new SimpleConfiguration();
		String configName="config.xml";
		String profileName="profile.xml";
		if ("dev".equals(System.getProperty("media.database")))
		{
			configName="config-dev.xml";
			profileName="profile-dev.xml";
		}
		File configFile;
		if (StringUtils.isEmpty(root))
		{
			System.err.println("catalina.home not found. Using user.dir instead");
			configFile=new File("conf", configName);
		}
		else
		{
			configuration.setApplicationDir(new File(root, "..").getAbsolutePath());
			configFile=new File(root, ".."+File.separator+"conf"+File.separator+configName);
		}
		System.out.println("Loading configuration from "+configFile.getAbsolutePath());
		configuration.loadDefaultsFromFile(configFile);
		try
		{
			String userValuesFile="media"+File.separator+profileName;
			log.info("Loading user configuration from "+userValuesFile);
			configuration.loadUserValues(userValuesFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void registerFormats()
	{
		super.registerFormats();

		HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
		rendererManager.setRenderer(Date.class, "Date only", new DefaultHTMLRenderer("Date only"));
		rendererManager.setRenderer(Date.class, "schedule", new DefaultHTMLRenderer("schedule"));
		rendererManager.setRenderer(String.class, "preformatted", new PreformattedHTMLRenderer());
		rendererManager.setRenderer(String.class, "html", new PlainHTMLRenderer());
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
		rendererManager.setRenderer(Book.class, new BookHTMLRenderer());
		rendererManager.setRenderer(Book.class, "series", new BookHTMLRenderer("series"));
		rendererManager.setRenderer(Show.class, new ShowHTMLRenderer());
		rendererManager.setRenderer(Genre.class, new GenreHTMLRenderer());
		rendererManager.setRenderer(Season.class, new SeasonHTMLRenderer());
		rendererManager.setRenderer(Season.class, "Menu", new SeasonHTMLRenderer("menulink"));
		rendererManager.setRenderer(LinkGroup.class, new LinkGroupHTMLRenderer());
		rendererManager.setRenderer(LinkGroup.class, "hierarchy", new LinkGroupHierarchyHTMLRenderer());
		rendererManager.setRenderer(ImageFile.class, new ImageFileHTMLRenderer());
		rendererManager.setRenderer(Channel.class, new ChannelHTMLRenderer());
		rendererManager.setRenderer(PhotoGallery.class, new PhotoGalleryHTMLRenderer());
		rendererManager.setRenderer(FanFic.class, new FanFicHTMLRenderer());
	}
}

