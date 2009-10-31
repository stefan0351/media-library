package com.kiwisoft.media.common;

import com.kiwisoft.media.files.MediaType;
import com.kiwisoft.media.files.MediaFile;

import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public interface MediaFilesAction
{
	public String getGalleryName();
	
	public MediaType getType();

	public Set<String> getMediaFileGroups();

	public Set<MediaFile> getMediaFiles(String group);
}
