package com.kiwisoft.media.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.management.*;
import java.io.IOException;
import java.util.Set;

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
		String path=System.getProperty("kiwisoft.web.autoStart.path");
		log.info("Starting browser for path: "+path);
		if (path!=null)
		{
			try
			{
				String port=getHttpPort();
				log.debug("port: "+port);
				String contextPath=servletConfig.getServletContext().getContextPath();
				log.debug("contextPath: "+contextPath);
				Runtime.getRuntime().exec("cmd /c start \"browser\" \"http://localhost:"+port+contextPath+path+"\"");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private String getHttpPort() throws Exception
	{
		for (MBeanServer mBeanServer : MBeanServerFactory.findMBeanServer(null))
		{
			Set<ObjectInstance> objectInstances=mBeanServer.queryMBeans(new ObjectName("Catalina:type=Connector,*"),
															   Query.eq(Query.attr("protocol"), Query.value("HTTP/1.1")));
			for (ObjectInstance objectInstance : objectInstances)
			{
				String port=objectInstance.getObjectName().getKeyProperty("port");
				if (port!=null) return port;
			}
		}
		return "8080";
	}

	@Override
	public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException
	{
	}
}
