package com.kiwisoft.media.photos;

import java.util.Set;
import java.io.File;
import java.awt.Dimension;

import com.kiwisoft.utils.Disposable;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.media.files.ImageFileInfo;
import com.kiwisoft.media.files.MediaFileUtils;
import com.kiwisoft.media.MediaConfiguration;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.collection.CollectionChangeSource;
import com.kiwisoft.persistence.DBLoader;

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

	public Set<PhotoGallery> getRootGalleries()
	{
		return DBLoader.getInstance().loadSet(PhotoGallery.class, null, "parent_id is null");
	}

	public PhotoGallery getGallery(Long id)
	{
		return DBLoader.getInstance().load(PhotoGallery.class, id);
	}

	public PhotoGallery createRootGallery()
	{
		PhotoGallery gallery=new PhotoGallery();
		addRootGallery(gallery);
		return gallery;
	}

	public void dropRootGallery(PhotoGallery gallery)
	{
		gallery.delete();
		removeRootGallery(gallery);
	}

	public void removeRootGallery(PhotoGallery gallery)
	{
		collectionChangeSupport.fireElementRemoved(GALLERIES, gallery);
	}

	public void addRootGallery(PhotoGallery gallery)
	{
		collectionChangeSupport.fireElementAdded(GALLERIES, gallery);
	}

	@Override
	public Disposable addCollectionListener(CollectionChangeListener listener)
	{
		return collectionChangeSupport.addListener(listener);
	}

	@Override
	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
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
