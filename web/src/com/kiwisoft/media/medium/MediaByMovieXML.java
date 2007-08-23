package com.kiwisoft.media.medium;

import java.io.IOException;
import java.util.TreeSet;
import java.util.Comparator;

import com.kiwisoft.media.XMLSource;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.utils.xml.XMLWriter;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class MediaByMovieXML implements XMLSource
{
	public void createXML(XMLWriter xmlWriter) throws IOException
	{
		TreeSet<Track> records=new TreeSet<Track>(new MyComparator());
		records.addAll(DBLoader.getInstance().loadSet(Track.class, "videos",
													 "videos.id=recordings.video_id" +
													 " and movie_id is not null and videos.userkey is not null" +
													 " and ifnull(videos.obsolete, 0)=0"));

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

	private static class MyComparator implements Comparator<Track>
	{
		public MyComparator()
		{
		}

		public int compare(Track o1, Track o2)
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
