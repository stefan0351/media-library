package com.kiwisoft.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.media.pics.PictureManager;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.gui.Icons;

public class PictureServlet extends HttpServlet
{
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		MediaManagerApp.getInstance(getServletContext());
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
			InputStream inputStream=null;
			String contentType=null;
			String type=request.getParameter("type");
			if ("PictureFile".equals(type))
			{
				Long id=new Long(request.getParameter("id"));
				PictureFile picture=PictureManager.getInstance().getPictureFile(id);
				File file=FileUtils.getFile(MediaConfiguration.getRootPath(), picture.getFile());
				if (file.exists())
				{
					inputStream=new FileInputStream(file);
					contentType="image/"+FileUtils.getExtension(file);
				}
				else
				{
					inputStream=Icons.getIconStream("no-photo-available");
				}
			}
			else if ("Icon".equals(type))
			{
				String name=request.getParameter("name");
				inputStream=Icons.getIconStream(name);
			}
			if (inputStream!=null)
			{
				response.setContentType(contentType);
				ServletOutputStream outputStream=response.getOutputStream();
				IOUtils.copy(inputStream, outputStream);
				outputStream.flush();
				inputStream.close();
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
