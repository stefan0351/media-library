package com.kiwisoft.media;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.utils.xml.XMLWriter;

public interface XMLSource
{
	void createXML(HttpServletRequest request, XMLWriter xmlWriter) throws IOException;
}
