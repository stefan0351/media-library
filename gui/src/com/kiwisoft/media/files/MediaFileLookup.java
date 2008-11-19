package com.kiwisoft.media.files;

import java.util.Collection;

import com.kiwisoft.swing.lookup.ListLookup;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 */
public class MediaFileLookup extends ListLookup<MediaFile>
{
	private int mediaType;

	public MediaFileLookup(int mediaType)
	{
		this.mediaType=mediaType;
	}

	public Collection<MediaFile> getValues(String text, MediaFile currentValue, boolean lookup)
	{
		if (text==null) return MediaFileManager.getInstance().getImages();
		else
		{
			if (text.indexOf('*')>=0) text=text.replace('*', '%');
			else text=text+"%";
			return DBLoader.getInstance().loadSet(MediaFile.class, null, "mediatype=? and name like ?", mediaType, text);
		}
	}
}
