package com.kiwisoft.media;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kiwisoft.app.Application;
import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.SplashWindow;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.dataImport.AmazonHttpHandler;
import com.kiwisoft.media.dataImport.LinkHttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MediaManager
{
	private final static Log log=LogFactory.getLog(MediaManager.class);

	public static SplashWindow splashWindow;

	public static void main(String[] args)
	{
		ExceptionHandler.init();
		Locale.setDefault(Locale.UK);
		new Application("media");
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

		splashWindow=new SplashWindow(Icons.getIcon("splash"));
		splashWindow.setStatus("MediaLib Version 3.0");
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
		startHttpListener(frame);
	}

	private static void startHttpListener(final MediaManagerFrame frame)
	{
		try
		{
			log.info("Starting HTTP server on port 50001...");
			final HttpServer httpServer=HttpServer.create(new InetSocketAddress("localhost", 50001), 0);
			httpServer.createContext("/amazon", new AmazonHttpHandler(frame));
			httpServer.createContext("/link", new LinkHttpHandler(frame));
			Runtime.getRuntime().addShutdownHook(new Thread()
			{
				@Override
				public void run()
				{
					System.out.print("Stopping listener...");
					httpServer.stop(0);
					System.out.println("done");
				}
			});
			httpServer.start();
			log.info("HTTP server started");
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			GuiUtils.handleThrowable(frame, e);
		}
	}

	private MediaManager()
	{
	}

}
