package com.kiwisoft.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.kiwisoft.media.pics.PictureFile;
import com.kiwisoft.media.pics.PictureManager;
import com.kiwisoft.utils.FileUtils;

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
			Long id=new Long(request.getParameter("picturefile_id"));
			PictureFile picture=PictureManager.getInstance().getPictureFile(id);
			File file=FileUtils.getFile(MediaConfiguration.getRootPath(), picture.getFile());
			String extension=FileUtils.getExtension(file);
			response.setContentType("image/"+extension);
			if (file.exists())
			{
				FileInputStream inputStream=new FileInputStream(file);
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
