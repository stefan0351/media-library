package com.kiwisoft.media;

import com.kiwisoft.media.medium.Track;
import com.kiwisoft.media.medium.TracksByTitleComparator;
import com.kiwisoft.media.medium.MediumManager;
import com.kiwisoft.media.medium.Medium;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Utils;

import java.util.TreeSet;
import java.util.Set;
import java.util.Comparator;

/**
 * @author Stefan Stiller
 * @since 02.10.2010
 */
public class PDFContext
{
	public Set<Track> getMovieTracks()
	{
		TreeSet<Track> records=new TreeSet<Track>(new TracksByTitleComparator());
		records.addAll(MediumManager.getInstance().getMovieTracks());
		return records;
	}

	public Set<Medium> getMedia()
	{
		Set<Medium> media=new TreeSet<Medium>(new MediumComparator());
		media.addAll(DBLoader.getInstance().loadSet(Medium.class, null, "userkey is not null and ifnull(obsolete, 0)=0"));
		return media;
	}

	private static class MediumComparator implements Comparator<Medium>
	{
		@Override
		public int compare(Medium o1, Medium o2)
		{
			return Utils.compareNullSafe(o1.getUserKey(), o2.getUserKey(), false);
		}
	}

}
