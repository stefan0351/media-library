package com.kiwisoft.media;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import javax.swing.UIManager;

import com.kiwisoft.media.ui.MediaManagerFrame;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.gui.SplashWindow;

public class MediaManager
{
	public static SplashWindow splashWindow;

	public static void main(String[] args)
	{
		Locale.setDefault(Locale.GERMANY);
		final Configurator configuration=Configurator.getInstance();
		configuration.determineBaseDirectory(Show.class);
		File configFile=new File(configuration.getApplicationBase(), "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		try
		{
			configuration.loadUserValues("media"+File.separator+"profile.xml");
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
			e.printStackTrace();
		}
		splashWindow=new SplashWindow("com/kiwisoft/media/icons/splash.jpg");
		splashWindow.setStatus("MediaManager Version 2.0");
		splashWindow.setVisible(true);

		MediaManagerFrame frame=new MediaManagerFrame();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				configuration.saveUserValues();
				super.windowClosing(e);
			}
		});
		splashWindow.dispose();
	}

	private MediaManager()
	{
	}
}
