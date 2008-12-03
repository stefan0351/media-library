package com.kiwisoft.media.pics;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.files.ImageFile;
import com.kiwisoft.media.files.MediaFileManager;

public class MediaFileServlet extends HttpServlet
{
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
		File rotatedFile=null;
		try
		{
			InputStream inputStream=null;
			String contentType=null;
			String type=request.getParameter("type");
			if ("ImageFile".equalsIgnoreCase(type) || "Image".equalsIgnoreCase(type) || "Media".equalsIgnoreCase(type))
			{
				Long id=new Long(request.getParameter("id"));
				String rotate=request.getParameter("rotate");
				ImageFile mediaFile;
				if ("ImageFile".equals(type)) mediaFile=MediaFileManager.getInstance().getImageFile(id);
				else mediaFile=MediaFileManager.getInstance().getMediaFile(id);
				File file=mediaFile.getPhysicalFile();
				if (file.exists())
				{
					String extension=FileUtils.getExtension(file);
					if (rotate!=null)
					{
						try
						{
							int angle=Integer.parseInt(rotate);
							if (angle!=0)
							{
								rotatedFile=File.createTempFile("pic", "."+extension, new File(System.getenv("TEMP")));
								MediaFileUtils.rotate(file, angle, rotatedFile);
								file=rotatedFile;
							}
						}
						catch (NumberFormatException e)
						{
							e.printStackTrace();
						}
					}
					inputStream=new FileInputStream(file);
					contentType=FileUtils.getMimeType(file);
					if (contentType==null) contentType=type.toLowerCase()+"/"+extension;
				}
				else
				{
					System.err.println("File "+file.getAbsolutePath()+" doesn't exist");
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
		finally
		{
			if (rotatedFile!=null)
			{
				try
				{
					rotatedFile.delete();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
