package com.kiwisoft.media;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kiwisoft.app.Application;
import com.kiwisoft.utils.UrlFormat;
import com.kiwisoft.utils.Time;
import com.kiwisoft.utils.TimeFormat;
import com.kiwisoft.format.FormatManager;
import com.kiwisoft.format.DefaultDateFormat;
import com.kiwisoft.format.DefaultTimeFormat;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileFormat;
import com.kiwisoft.media.photos.PhotoGallery;
import com.kiwisoft.media.photos.PhotoGalleryFormat;
import com.kiwisoft.media.person.Gender;
import com.kiwisoft.media.person.GenderFormat;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.CreditTypeFormat;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.FanDomLinkableFormat;
import com.kiwisoft.media.download.WebFolder;
import com.kiwisoft.media.download.WebDocument;
import com.kiwisoft.media.download.WebFolderFormat;
import com.kiwisoft.media.download.WebDocumentFormat;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.EpisodeFormat;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.cfg.SimpleConfiguration;

/**
 * @author Stefan Stiller
 * @todo Check movie by imdb_key
 */
public class MediaApplication extends Application
{
	private final static Log log=LogFactory.getLog(MediaApplication.class);

	public MediaApplication()
	{
		super("media");
	}

	@Override
	public void configureXML() throws IOException
	{
		Icons.setResource("/com/kiwisoft/media/icons/Icons.xml");
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		log.info("Loading default configuration from "+configFile.getAbsolutePath());
		configuration.loadDefaultsFromFile(configFile);
		try
		{
			String userValuesFile="media"+File.separator+"profile.xml";
			log.info("Loading user configuration from "+userValuesFile);
			configuration.loadUserValues(userValuesFile);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		if (configuration.getBoolean("proxy.use", false))
		{
			System.setProperty("http.proxyHost", configuration.getString("proxy.host"));
			System.setProperty("http.proxyPort", configuration.getString("proxy.port"));
		}
		try
		{
			UIManager.setLookAndFeel("com.incors.plaf.alloy.AlloyLookAndFeel");
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		UIManager.put("MenuItem.checkIcon", Icons.ICON_1X1);
		UIManager.put("MenuItem.arrayIcon", Icons.ICON_1X1);
	}

	@Override
	protected void registerFormats()
	{
		FormatManager formatManager=FormatManager.getInstance();
		formatManager.setFormat(MediaFile.class, new MediaFileFormat());
		formatManager.setFormat(PhotoGallery.class, new PhotoGalleryFormat());
		formatManager.setFormat(LinkGroup.class, "hierarchy", new LinkGroupHierarchyFormat());
		formatManager.setFormat(LinkGroup.class, new LinkGroupFormat());
		formatManager.setFormat(Link.class, new LinkFormat());
		formatManager.setFormat(Language.class, new LanguageFormat());
		formatManager.setFormat(Country.class, new CountryFormat());
		formatManager.setFormat(Gender.class, new GenderFormat());
		formatManager.setFormat(CreditType.class, new CreditTypeFormat());
		formatManager.setFormat(FanDom.class, "linkable", new FanDomLinkableFormat());
		formatManager.setFormat(WebFolder.class, new WebFolderFormat());
		formatManager.setFormat(WebDocument.class, new WebDocumentFormat());
		formatManager.setFormat(URL.class, new UrlFormat());
		formatManager.setFormat(Date.class, "schedule", new DefaultDateFormat(new SimpleDateFormat("EE, dd.MM.yy HH:mm")));
		formatManager.setFormat(Time.class, "mediafile", new DefaultTimeFormat(new TimeFormat("H:mm:ss")));
		formatManager.setFormat(Episode.class, new EpisodeFormat(false));
		formatManager.setFormat(Episode.class, "full", new EpisodeFormat(true));
		super.registerFormats();
	}
}
