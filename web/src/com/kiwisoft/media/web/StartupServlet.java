package com.kiwisoft.media.web;

import java.io.IOException;
import javax.servlet.*;

import com.kiwisoft.media.MediaWebApplication;

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

	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException
	{
	}
}
