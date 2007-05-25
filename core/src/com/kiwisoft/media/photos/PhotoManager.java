package com.kiwisoft.media.photos;

import java.util.Set;
import java.io.File;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.awt.Dimension;

import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.gui.ImageUtils;
import com.kiwisoft.media.pics.ImageData;
import com.kiwisoft.media.MediaConfiguration;

/**
 * @author Stefan Stiller
 */
public class PhotoManager implements CollectionChangeSource
{
	private static PhotoManager instance;
	public static final String GALLERIES="galleries";

	public static PhotoManager getInstance()
	{
		if (instance==null) instance=new PhotoManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private PhotoManager()
	{
	}

	public Set<PhotoGallery> getGalleries()
	{
		return DBLoader.getInstance().loadSet(PhotoGallery.class);
	}

	public PhotoGallery getGallery(Long id)
	{
		return DBLoader.getInstance().load(PhotoGallery.class, id);
	}

	public PhotoGallery createGallery()
	{
		PhotoGallery gallery=new PhotoGallery();
		collectionChangeSupport.fireElementAdded(GALLERIES, gallery);
		return gallery;
	}

	public void dropGallery(PhotoGallery gallery)
	{
		gallery.delete();
		collectionChangeSupport.fireElementRemoved(GALLERIES, gallery);
	}

	public void addCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	public ImageData createThumbnail(File file, int rotation)
	{
		NumberFormat numberFormat=new DecimalFormat("000000");
		File thumbnailFile;
		do
		{
			thumbnailFile=new File(MediaConfiguration.getPhotoThumbnailPath(), numberFormat.format(MediaConfiguration.nextThumbnailId())+".jpg");
		}
		while (thumbnailFile.exists());
		ImageUtils.rotateAndResize(file, rotation, Photo.THUMBNAIL_WIDTH, Photo.THUMBNAIL_HEIGHT, thumbnailFile);
		Dimension size=ImageUtils.getImageSize(thumbnailFile);
		if (size!=null) return new ImageData(thumbnailFile, size);
		return null;
	}
}
