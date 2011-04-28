package com.kiwisoft.media.photos;

import java.util.Set;
import java.io.File;
import java.awt.Dimension;

import com.kiwisoft.utils.Bean;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.files.ImageFileInfo;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.PropertyChangeSource;

/**
 * @author Stefan Stiller
 */
public class PhotoManager extends Bean implements PropertyChangeSource
{
	private static PhotoManager instance;
	public static final String GALLERIES="galleries";

	public static PhotoManager getInstance()
	{
		if (instance==null) instance=new PhotoManager();
		return instance;
	}

	private PhotoManager()
	{
	}

	public PhotoGallery getRootGallery()
	{
		return getGallery(1L);
	}

	public Set<PhotoGallery> getGalleries()
	{
		return DBLoader.getInstance().loadSet(PhotoGallery.class);
	}

	public PhotoGallery getGallery(Long id)
	{
		return DBLoader.getInstance().load(PhotoGallery.class, id);
	}

	public ImageFileInfo createThumbnail(File file, int rotation)
	{
		String photoPath=FileUtils.getRelativePath(MediaConfiguration.getPhotosPath(), file.getAbsolutePath());
		String thumbnailPath=MediaFileUtils.getThumbnailPath(photoPath, "thb", "jpg");
		File thumbnailFile=FileUtils.getFile(MediaConfiguration.getRootPath(), "photos", "thumbnails", thumbnailPath);
		thumbnailFile.getParentFile().mkdirs();
		MediaFileUtils.rotateAndResize(file, rotation, MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT, thumbnailFile);
		Dimension size=MediaFileUtils.getImageSize(thumbnailFile);
		if (size!=null) return new ImageFileInfo(thumbnailFile, size);
		return null;
	}
}
