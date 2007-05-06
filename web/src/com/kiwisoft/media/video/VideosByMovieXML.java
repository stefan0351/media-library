package com.kiwisoft.media.video;

import java.io.IOException;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.movie.MovieComparator;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class VideosByMovieXML implements XMLSource
{
	public void createXML(XMLWriter xmlWriter) throws IOException
	{
		TreeSet<Recording> records=new TreeSet<Recording>(new MyComparator());
		records.addAll(DBLoader.getInstance().loadSet(Recording.class, "videos",
													 "videos.id=recordings.video_id" +
													 " and movie_id is not null and videos.userkey is not null" +
													 " and ifnull(videos.obsolete, 0)=0"));

		xmlWriter.startElement("records");
		for (Recording record : records)
		{
			xmlWriter.startElement("record");
			xmlWriter.setAttribute("name", record.getName());
			Video video=record.getVideo();
			xmlWriter.setAttribute("mediumType", video.getType().getName());
			xmlWriter.setAttribute("mediumKey", video.getFullKey());
			xmlWriter.setAttribute("storage", video.getStorage());
			xmlWriter.setAttribute("language", record.getLanguage().getSymbol());
			xmlWriter.closeElement("record");
		}
		xmlWriter.closeElement("records");
	}

	private static class MyComparator implements Comparator<Recording>
	{
		public MyComparator()
		{
		}

		public int compare(Recording o1, Recording o2)
		{
			Movie movie1=o1.getMovie();
			Movie movie2=o2.getMovie();
			int result=Utils.compareNullSafe(movie1.getIndexBy(o1.getLanguage()), movie2.getIndexBy(o2.getLanguage()), true);
			if (result==0) result=Utils.compareNullSafe(o1.getEvent(), o2.getEvent(), true);
			if (result==0) result=Utils.compareNullSafe(movie1.getYear(), movie2.getYear(), false);
			if (result==0) result=o1.getId().compareTo(o2.getId());
			return result;
		}
	}
}
