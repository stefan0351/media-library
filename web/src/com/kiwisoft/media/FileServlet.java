package com.kiwisoft.media;

import com.kiwisoft.utils.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileServlet extends HttpServlet
{
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

	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String path=request.getPathInfo().replace('/', File.separatorChar);
		if (path.startsWith(File.separator)) path=path.substring(1);
		File file=new File(MediaConfiguration.getRootPath(), path);
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