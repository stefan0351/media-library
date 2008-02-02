package com.kiwisoft.media;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import javax.swing.UIManager;

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
	public static SplashWindow splashWindow;

	public static void main(String[] args)
	{
		Locale.setDefault(Locale.UK);
		new Application("media");
		Icons.setResource("/com/kiwisoft/media/icons/Icons.xml");
		SimpleConfiguration configuration=new SimpleConfiguration();
		configuration.loadDefaultsFromResource("config.xml");
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
		startHttpListener(frame);
	}

	private static void startHttpListener(final MediaManagerFrame frame)
	{
		try
		{
			System.out.print("Starting listener on port 50001...");
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
			System.out.println("done");
		}
		catch (IOException e)
		{
			GuiUtils.handleThrowable(frame, e);
		}
	}

	private MediaManager()
	{
	}

}
