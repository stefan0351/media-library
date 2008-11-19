package com.kiwisoft.media.files;

import java.util.Set;

import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSource;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.Disposable;

/**
 * @author Stefan Stiller
 */
public class MediaFileManager implements CollectionChangeSource
{
	public static final String IMAGES="images";
	public static final String VIDEOS="videos";

	private static MediaFileManager instance;

	public static MediaFileManager getInstance()
	{
		if (instance==null) instance=new MediaFileManager();
		return instance;
	}

	private MediaFileManager()
	{
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public Set<MediaFile> getImages()
	{
		return getMediaFiles(MediaFile.IMAGE);
	}

	public Set<MediaFile> getMediaFiles(int mediaType)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class, null, "mediatype=?", mediaType);
	}

	public Set<MediaFile> getImageByFile(String root, String relativePath)
	{
		return DBLoader.getInstance().loadSet(MediaFile.class, null, "mediatype=? and file=? and root=?", MediaFile.IMAGE, relativePath, root);
	}

	public MediaFile createImage(String root)
	{
		MediaFile image=new MediaFile(MediaFile.IMAGE, root);
		fireElementAdded(IMAGES, image);
		return image;
	}

	public MediaFile createVideo(String root)
	{
		MediaFile video=new MediaFile(MediaFile.VIDEO, root);
		fireElementAdded(VIDEOS, video);
		return video;
	}

	public void dropImage(MediaFile mediaFile)
	{
		mediaFile.delete();
		fireElementRemoved(IMAGES, mediaFile);
	}

	public MediaFile getImage(Long id)
	{
		return DBLoader.getInstance().load(MediaFile.class, null, "id=? and mediatype=?", id, MediaFile.IMAGE);
	}

	public MediaFile getMediaFile(Long id)
	{
		return DBLoader.getInstance().load(MediaFile.class, id);
	}

	public ImageFile getImageFile(Long id)
	{
		return DBLoader.getInstance().load(ImageFile.class, id);
	}

	public Disposable addCollectionListener(CollectionChangeListener listener)
	{
		return collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	protected void fireElementAdded(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementAdded(propertyName, element);
	}

	protected void fireElementRemoved(String propertyName, Object element)
	{
		collectionChangeSupport.fireElementRemoved(propertyName, element);
	}
}
