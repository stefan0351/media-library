package com.kiwisoft.media.medium;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.utils.xml.XMLWriter;

/**
 * @author Stefan Stiller
 */
public class DVDCoverXML implements XMLSource
{
	public void createXML(HttpServletRequest request, XMLWriter xmlWriter) throws IOException
	{
		Medium medium=MediumManager.getInstance().getMedium(Long.valueOf(request.getParameter("medium")));

		xmlWriter.startElement("medium");
		xmlWriter.setAttribute("key", medium.getFullKey());
		xmlWriter.setAttribute("name", medium.getName());
		xmlWriter.setAttribute("length", medium.getLength());
		xmlWriter.setAttribute("remaining", medium.getRemainingLength());
		xmlWriter.setAttribute("storage", medium.getStorage());
		xmlWriter.closeElement("medium");
	}
}
