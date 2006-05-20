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
import javax.servlet.http.HttpSession;

import com.kiwisoft.utils.Configurator;
import com.kiwisoft.media.video.VideoManager;

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
		String path=context.getRealPath("WEB-INF/config/config.xml");
		Configurator.getInstance().loadDefaultsFromFile(new File(path));
	}
}

