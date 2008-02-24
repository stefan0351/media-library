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
import org.apache.commons.io.FilenameUtils;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.xp.XPLoader;
import com.kiwisoft.xp.XPBean;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;

public class ResourceServlet extends HttpServlet
{
	private ResourceBundle templates;

	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		MediaManagerApp.getInstance();
		templates=ResourceBundle.getBundle("com.kiwisoft.xp.templates");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String resource=request.getParameter("file");
			System.out.println("Loading resource "+resource);
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
					String contentType=null;
					MimeEntry mimeEntry=MimeTable.getDefaultTable().findByFileName(file.getName());
					if (mimeEntry!=null) contentType=mimeEntry.getType();
					else
					{
						String extension=FilenameUtils.getExtension(file.getName()).toLowerCase();
						if ("mp3".equals(extension)) contentType="audio/mp3";
						else System.err.println("Mimetype not found for "+extension);
					}
					if (file.exists())
					{
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
