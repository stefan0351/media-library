package com.kiwisoft.media;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import javax.swing.UIManager;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.SplashWindow;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.app.Application;

public class MediaManager
{
	public static SplashWindow splashWindow;

	public static void main(String[] args)
	{
		Locale.setDefault(Locale.UK);
		new Application("media");
		Icons.setResource("/com/kiwisoft/media/icons/Icons.xml");
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File(FileUtils.getRootDirectory(Show.class), "config.xml");
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
		UIManager.put("MenuItem.checkIcon", Icons.ICON_1X1);
		UIManager.put("MenuItem.arrayIcon", Icons.ICON_1X1);

		splashWindow=new SplashWindow(Icons.getIcon("splash"));
		splashWindow.setStatus("MediaManager Version 2.0");
		splashWindow.setVisible(true);

		MediaManagerFrame frame=new MediaManagerFrame();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				Configuration.getInstance().saveUserValues();
				super.windowClosing(e);
			}
		});
		splashWindow.dispose();
	}

	private MediaManager()
	{
	}
}
