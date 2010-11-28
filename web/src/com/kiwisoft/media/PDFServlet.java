package com.kiwisoft.media;

import org.apache.fop.apps.*;
import org.apache.fop.servlet.ServletContextURIResolver;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.EscapeXmlReference;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.VelocityContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;
import java.io.*;

public class PDFServlet extends HttpServlet
{
	private URIResolver uriResolver;
	private TransformerFactory transformerFactory;
	private FopFactory fopFactory;
	private VelocityEngine velocityEngine;

	@Override
	public void init() throws ServletException
	{
		uriResolver=new ServletContextURIResolver(getServletContext());

		transformerFactory=TransformerFactory.newInstance();
		transformerFactory.setURIResolver(uriResolver);

		//Configure FopFactory as desired
		fopFactory=FopFactory.newInstance();
		fopFactory.setURIResolver(uriResolver);

		velocityEngine=new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_REFERENCEINSERTION, EscapeXmlReference.class.getName());
		velocityEngine.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
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
		ServletContext servletContext=request.getSession().getServletContext();
		try
		{
			String templateName=request.getParameter("template");
			File templateFile=new File(servletContext.getRealPath(templateName));

			VelocityContext velocityContext=new VelocityContext();
			velocityContext.put("context", new PDFContext());

			StringWriter velocityWriter=new StringWriter();

			velocityEngine.evaluate(velocityContext, velocityWriter, templateName, new FileReader(templateFile));

			StreamSource foSource=new StreamSource(new StringReader(velocityWriter.toString()));

			//Setup the identity transformation
			Transformer transformer=transformerFactory.newTransformer();
			transformer.setURIResolver(uriResolver);

			//Start transformation and rendering process
			render(foSource, transformer, response);
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

	/**
	 * Renders an input file (XML or XSL-FO) into a PDF file. It uses the JAXP
	 * transformer given to optionally transform the input document to XSL-FO.
	 * The transformer may be an identity transformer in which case the input
	 * must already be XSL-FO. The PDF is written to a byte array that is
	 * returned as the method's result.
	 *
	 * @param src		 Input XML or XSL-FO
	 * @param transformer Transformer to use for optional transformation
	 * @param response	HTTP response object
	 * @throws FOPException		 If an error occurs during the rendering of the
	 *                              XSL-FO
	 * @throws TransformerException If an error occurs during XSL
	 *                              transformation
	 * @throws IOException		  In case of an I/O problem
	 */
	protected void render(Source src, Transformer transformer, HttpServletResponse response)
			throws FOPException, TransformerException, IOException
	{

		FOUserAgent foUserAgent=fopFactory.newFOUserAgent();

		//Setup output
		org.apache.commons.io.output.ByteArrayOutputStream out=new org.apache.commons.io.output.ByteArrayOutputStream();

		//Setup FOP
		Fop fop=fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

		//Make sure the XSL transformation's result is piped through to FOP
		Result res=new SAXResult(fop.getDefaultHandler());

		//Start the transformation and rendering process
		transformer.transform(src, res);

		//Return the result
		sendPDF(out.toByteArray(), response);
	}

	private void sendPDF(byte[] content, HttpServletResponse response) throws IOException
	{
		//Send the result back to the client
		response.setContentType("application/pdf");
		response.setContentLength(content.length);
		response.getOutputStream().write(content);
		response.getOutputStream().flush();
	}


}
