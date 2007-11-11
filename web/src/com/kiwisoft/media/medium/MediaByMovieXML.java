package com.kiwisoft.media.medium;

import java.io.IOException;
import java.util.TreeSet;
import java.util.Set;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 */
public class MediaByMovieXML implements XMLSource
{
	public void createXML(XMLWriter xmlWriter) throws IOException
	{
		TreeSet<Track> records=new TreeSet<Track>(new TracksByTitleComparator());
		records.addAll(MediumManager.getInstance().getMovieTracks());

		xmlWriter.startElement("tracks");
		for (Track record : records)
		{
			xmlWriter.startElement("track");
			xmlWriter.setAttribute("name", record.getName());
			Medium video=record.getMedium();
			xmlWriter.setAttribute("mediumType", video.getType().getName());
			xmlWriter.setAttribute("mediumKey", video.getFullKey());
			xmlWriter.setAttribute("storage", video.getStorage());
			xmlWriter.setAttribute("language", record.getLanguage().getSymbol());
			xmlWriter.closeElement("track");
		}
		xmlWriter.closeElement("tracks");
	}


}
