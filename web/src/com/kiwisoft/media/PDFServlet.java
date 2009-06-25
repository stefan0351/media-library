package com.kiwisoft.media;

import java.io.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.*;

import com.kiwisoft.utils.xml.XMLWriter;

public class PDFServlet extends HttpServlet
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

	private void process(HttpServletRequest request, HttpServletResponse response)
	{
		ServletContext servletContext=request.getSession().getServletContext();
		try
		{
			String xmlSourceName=request.getParameter("xml.source");
			XMLSource xmlSource=(XMLSource)Class.forName(xmlSourceName).newInstance();
			String xslPath=request.getParameter("xsl");
			File xslFile=new File(servletContext.getRealPath(xslPath));
			convertXML2PDF(response, createXML(request, xmlSource), xslFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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

	private Reader createXML(HttpServletRequest request, XMLSource xmlSource) throws IOException
	{
		StringWriter writer=new StringWriter();
		XMLWriter xmlWriter=new XMLWriter(writer, null);
		xmlWriter.start();
		xmlSource.createXML(request, xmlWriter);
		xmlWriter.close();
		return new StringReader(writer.toString());
	}

	public void convertXML2PDF(HttpServletResponse response, Reader xml, File xslt) throws IOException, FOPException, TransformerException
	{
		response.setContentType("application/pdf");

		FopFactory fopFactory=FopFactory.newInstance();
		FOUserAgent foUserAgent=fopFactory.newFOUserAgent();

		// Setup output
		OutputStream outputStream=response.getOutputStream();
		outputStream=new BufferedOutputStream(outputStream);

		// Construct fop with desired output format
		Fop fop=fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);

		// Setup XSLT
		TransformerFactory factory=TransformerFactory.newInstance();
		Transformer transformer=factory.newTransformer(new StreamSource(xslt));

		// Start XSLT transformation and FOP processing
		transformer.transform(new StreamSource(xml), new SAXResult(fop.getDefaultHandler()));

		outputStream.flush();
	}
}
