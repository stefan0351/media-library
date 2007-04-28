package com.kiwisoft.media.photos;

import java.util.Set;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.Icon;

import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.gui.ImageUtils;

/**
 * @author Stefan Stiller
 */
public class PhotoManager implements CollectionChangeSource
{
	private static PhotoManager instance;
	public static final String BOOKS="books";

	public static PhotoManager getInstance()
	{
		if (instance==null) instance=new PhotoManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private PhotoManager()
	{
	}

	public Set<PhotoBook> getBooks()
	{
		return DBLoader.getInstance().loadSet(PhotoBook.class);
	}

	public PhotoBook getBook(Long id)
	{
		return DBLoader.getInstance().load(PhotoBook.class, id);
	}

	public PhotoBook createBook()
	{
		PhotoBook book=new PhotoBook();
		collectionChangeSupport.fireElementAdded(BOOKS, book);
		return book;
	}


	public void dropBook(PhotoBook book)
	{
		book.delete();
		collectionChangeSupport.fireElementRemoved(BOOKS, book);
	}

	public void addCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
	}

	public void removeCollectionListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.removeListener(listener);
	}

	public Icon getThumbnail(Photo photo)
	{
		File originalFile=new File(photo.getOriginalFile());
		String extension=FileUtils.getExtension(originalFile);
		String thumbnailPath=Configurator.getInstance().getString("path.photos.thumbnails");
		File thumbnailFile=new File(thumbnailPath, photo.getId()+"_"+photo.getRotation()+"."+extension.toLowerCase());
		if (!thumbnailFile.exists())
		{
			new File(thumbnailPath).mkdirs();
			ImageUtils.rotateAndResize(originalFile, photo.getRotation(), 160, 160, thumbnailFile);
		}
		try
		{
			return new ImageIcon(thumbnailFile.toURL());
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
