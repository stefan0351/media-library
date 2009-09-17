package com.kiwisoft.media.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Stefan Stiller
 * @since 17.09.2009
 */
public class BrowserServlet extends GenericServlet
{
	private final static Log log=LogFactory.getLog(BrowserServlet.class);

	@Override
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		String autoStartUrl=System.getProperty("kiwisoft.web.autoStart.url");
		if (autoStartUrl!=null)
		{
			log.info("url: "+autoStartUrl);
			try
			{
				Runtime.getRuntime().exec("cmd /c start \"browser\" \""+autoStartUrl+"\"");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException
	{
	}
}
