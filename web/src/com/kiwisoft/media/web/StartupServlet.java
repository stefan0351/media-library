package com.kiwisoft.media.web;

import java.io.IOException;
import javax.servlet.*;

import com.kiwisoft.media.MediaWebApplication;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
 */

public class StartupServlet extends GenericServlet
{
	@Override
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		MediaWebApplication.checkInstance();
	}

	@Override
	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException
	{
	}

	@Override
	public void destroy()
	{
		DBSession.shutdown();
		super.destroy();
	}
}
