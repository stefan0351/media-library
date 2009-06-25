package com.kiwisoft.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.xp.XPLoader;
import com.kiwisoft.xp.XPBean;

public class ResourceServlet extends HttpServlet
{
    private final static Log log=LogFactory.getLog(ResourceServlet.class);

    private ResourceBundle templates;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		templates=ResourceBundle.getBundle("com.kiwisoft.xp.templates");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String resource=request.getParameter("file");
            log.debug("resource="+resource);
            if (!StringUtils.isEmpty(resource) && !resource.startsWith("/") && !resource.startsWith("\\") && !resource.startsWith(".."))
			{
				File file=new File(MediaConfiguration.getRootPath(), resource);
				if (resource.endsWith(".xp"))
				{
					Object bean=XPLoader.loadXMLFile(request, file);
					XPBean xmlBean=(XPBean) bean;
					request.setAttribute("xp", xmlBean);
					String templateFile=(String)xmlBean.getValue("template");
					if (templateFile==null)
					{
						Object variant=xmlBean.getValue("variant");
						if (variant!=null) templateFile=templates.getString(xmlBean.getName()+"."+variant);
						else templateFile=templates.getString(xmlBean.getName());
					}
					if (StringUtils.isEmpty(templateFile)) throw new RuntimeException("No template defined for name="+xmlBean.getName()
							+"; variant="+xmlBean.getValue("variant"));
					RequestDispatcher requestDispatcher=request.getRequestDispatcher(templateFile);
					requestDispatcher.forward(request, response);
				}
				else
				{
					if (file.exists())
					{
						String contentType=FileUtils.getMimeType(file);
						InputStream inputStream=new FileInputStream(file);
						response.setContentType(contentType);
						ServletOutputStream outputStream=response.getOutputStream();
						IOUtils.copy(inputStream, outputStream);
						outputStream.flush();
						inputStream.close();
					}
				}
			}
		}
		catch (Exception e)
		{
			ServletContext servletContext=request.getSession().getServletContext();
			request.setAttribute("error", e);
			RequestDispatcher requestDispatcher=servletContext.getRequestDispatcher("/error.jsp");
			try
			{
				requestDispatcher.forward(request, response);
			}
			catch (Exception e1)
			{
			}
		}
	}
}
