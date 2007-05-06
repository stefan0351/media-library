package com.kiwisoft.media.video;

import java.util.TreeSet;
import java.util.Comparator;
import java.io.IOException;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class VideosByKeyXML implements XMLSource
{
	public void createXML(XMLWriter xmlWriter) throws IOException
	{
		TreeSet<Video> videos=new TreeSet<Video>(new VideoComparator());
		videos.addAll(DBLoader.getInstance().loadSet(Video.class, null, "userkey is not null and ifnull(obsolete, 0)=0"));

		xmlWriter.startElement("videos");
		for (Video video : videos)
		{
			xmlWriter.startElement("video");
			xmlWriter.setAttribute("key", video.getFullKey());
			xmlWriter.setAttribute("name", video.getName());
			xmlWriter.setAttribute("length", video.getLength());
			xmlWriter.setAttribute("remaining", video.getRemainingLength());
			xmlWriter.setAttribute("storage", video.getStorage());
			xmlWriter.closeElement("video");
		}
		xmlWriter.closeElement("videos");
	}

	private static class VideoComparator implements Comparator<Video>
	{
		public int compare(Video o1, Video o2)
		{
			return Utils.compareNullSafe(o1.getUserKey(), o2.getUserKey(), false);
		}
	}
}
