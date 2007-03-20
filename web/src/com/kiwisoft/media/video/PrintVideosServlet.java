package com.kiwisoft.media.video;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.messaging.MessageHandler;

import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.video.VideoManager;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.MediaManagerApp;
import com.kiwisoft.utils.xml.XMLWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 25.06.2004
 * Time: 18:14:10
 * To change this template use File | Settings | File Templates.
 */
public class PrintVideosServlet extends HttpServlet
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
		ServletContext servletContext=request.getSession().getServletContext();
		try
		{
			File xslFile=new File(servletContext.getRealPath(request.getParameter("xsl")));
			convertXML2PDF(response, createXML(), xslFile);
		}
		catch (Exception e)
		{
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

	private Reader createXML() throws IOException
	{
		StringWriter writer=new StringWriter();
		XMLWriter xmlWriter=new XMLWriter(writer, null);
		xmlWriter.start();
		xmlWriter.startElement("videos");

		TreeSet mediumTypes=new TreeSet(new MediumComparator());
		mediumTypes.addAll(MediumType.getAll());
		for (Iterator itTypes=mediumTypes.iterator(); itTypes.hasNext();)
		{
			MediumType mediumType=(MediumType)itTypes.next();
			TreeSet videos=new TreeSet(new VideoComparator());
			videos.addAll(VideoManager.getInstance().getVideos(mediumType));

			if (!videos.isEmpty())
			{
				xmlWriter.startElement("mediumType");
				xmlWriter.setAttribute("name", mediumType.getPluralName());

				for (Iterator it=videos.iterator(); it.hasNext();)
				{
					Video video=(Video)it.next();
					xmlWriter.startElement("video");
					if (video.getUserKey()!=null) xmlWriter.setAttribute("key", video.getUserKey());
					xmlWriter.setAttribute("name", video.getName());
					xmlWriter.setAttribute("length", video.getLength());
					xmlWriter.setAttribute("remaining", video.getRemainingLength());
					for (Iterator itRecords=video.getRecordings().iterator(); itRecords.hasNext();)
					{
						Recording recording=(Recording)itRecords.next();
						xmlWriter.startElement("recording");
						xmlWriter.setAttribute("name", recording.getName());
						xmlWriter.setAttribute("length", recording.getLength());
						xmlWriter.closeElement("recording");
					}
					xmlWriter.closeElement("video");
				}
				xmlWriter.closeElement("mediumType");
			}
		}

		xmlWriter.closeElement("videos");
		xmlWriter.close();
		return new StringReader(writer.toString());
	}


	public void convertXML2PDF(HttpServletResponse response, Reader xml, File xslt) throws IOException, FOPException, TransformerException
	{
		response.setContentType("application/pdf");

		//Construct driver
		Driver driver=new Driver();

		//Setup logger
		Logger logger=new ConsoleLogger(ConsoleLogger.LEVEL_ERROR);
		driver.setLogger(logger);
		MessageHandler.setScreenLogger(logger);

		//Setup Renderer (output format)
		driver.setRenderer(Driver.RENDER_PDF);

		//Setup output
		OutputStream outputStream=response.getOutputStream();
		driver.setOutputStream(outputStream);

		//Setup XSLT
		TransformerFactory factory=TransformerFactory.newInstance();
		Transformer transformer=factory.newTransformer(new StreamSource(xslt));

		//Setup input for XSLT transformation
		Source src=new StreamSource(xml);

		//Resulting SAX events (the generated FO) must be piped through to FOP
		Result res=new SAXResult(driver.getContentHandler());

		//Start XSLT transformation and FOP processing
		transformer.transform(src, res);

		outputStream.flush();
	}

	private static class VideoComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			Video v1=(Video)o1;
			Video v2=(Video)o2;
			String key1=v1.getUserKey();
			String key2=v2.getUserKey();
			if (key1==null) key1="";
			if (key2==null) key2="";
			int result=key1.compareToIgnoreCase(key2);
			if (result==0) return v1.getName().compareToIgnoreCase(v2.getName());
			return result;
		}
	}

	private static class MediumComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			MediumType m1=(MediumType)o1;
			MediumType m2=(MediumType)o2;
			return m1.getId().compareTo(m2.getId());
		}
	}
}
