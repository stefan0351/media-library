package com.kiwisoft.media;

import java.io.IOException;

import com.kiwisoft.utils.xml.XMLWriter;

public interface XMLSource
{
	void createXML(XMLWriter xmlWriter) throws IOException;
}
