package com.kiwisoft.media;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kiwisoft.app.Application;
import com.kiwisoft.cfg.Configuration;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.SplashWindow;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.dataimport.LinkHttpHandler;
import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.files.MediaFileIconFormat;
import com.kiwisoft.format.FormatManager;
import com.sun.net.httpserver.HttpServer;

public class MediaManager
{
	private final static Log log=LogFactory.getLog(MediaManager.class);

	public static SplashWindow splashWindow;

	public static void main(String[] args) throws IOException
	{
		Locale.setDefault(Locale.UK);
		Application application=new MediaApplication()
		{
			@Override
			protected void registerFormats()
			{
				super.registerFormats();
				FormatManager.getInstance().setFormat(MediaFile.class, "icon", new MediaFileIconFormat());
			}
		};
		application.configureXML();
		application.initialize();

		splashWindow=new SplashWindow(Icons.getIcon("splash"));
		splashWindow.setStatus("MediaLib Version 3.0");
		splashWindow.setVisible(true);

		MediaManagerFrame frame=new MediaManagerFrame();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
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
