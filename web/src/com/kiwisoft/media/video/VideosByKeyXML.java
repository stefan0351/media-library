package com.kiwisoft.media.video;

import java.util.TreeSet;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.StringUtils;

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
			if (video.getUserKey()!=null) xmlWriter.setAttribute("key", video.getUserKey());
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
		private Pattern pattern;

		public VideoComparator()
		{
			pattern=VideoManager.getInstance().getKeyPattern();
		}

		public int compare(Video o1, Video o2)
		{
			String key1=StringUtils.null2Empty(o1.getUserKey());
			String key2=StringUtils.null2Empty(o2.getUserKey());
			Matcher matcher1=pattern.matcher(key1);
			Matcher matcher2=pattern.matcher(key2);
			if (matcher1.matches() && matcher2.matches())
			{
				int result=matcher1.group(1).compareToIgnoreCase(matcher2.group(1));
				if (result==0) result=new Integer(matcher1.group(2)).compareTo(new Integer(matcher2.group(2)));
				return result;
			}
			return key1.compareToIgnoreCase(key2);
		}
	}
}
