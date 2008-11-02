package com.kiwisoft.media.pics;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.awt.Dimension;

import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.collection.CollectionChangeSource;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.Disposable;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 */
public class PictureManager implements CollectionChangeSource
{
	public static final String PICTURES="pictures";

	private static PictureManager instance;
	final static String[] THUMBNAIL_SUFFIXES={"mini", "small", "sb"};

	public static PictureManager getInstance()
	{
		if (instance==null) instance=new PictureManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public Set<Picture> getPictures()
	{
		return DBLoader.getInstance().loadSet(Picture.class);
	}

	public Set<Picture> getPictureByFile(String root, String relativePath)
	{
		return DBLoader.getInstance().loadSet(Picture.class, null, "file=? and root=?", relativePath, root);
	}

	public Picture createPicture(String root)
	{
		Picture picture=new Picture(root);
		fireElementAdded(PICTURES, picture);
		return picture;
	}

	public void dropPicture(Picture picture)
	{
		picture.delete();
		fireElementRemoved(PICTURES, picture);
	}

	public Picture getPicture(Long id)
	{
		return DBLoader.getInstance().load(Picture.class, id);
	}

	public PictureFile getPictureFile(Long id)
	{
		return DBLoader.getInstance().load(PictureFile.class, id);
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

	public static Map<String, ImageData> getThumbnails(File imageFile)
	{
		Map<String, ImageData> map=new HashMap<String, ImageData>();
		String extension=FileUtils.getExtension(imageFile);
		String basePath=imageFile.getAbsolutePath();
		basePath=basePath.substring(0, basePath.length()-extension.length()-1);
		for (String suffix : THUMBNAIL_SUFFIXES)
		{
			File file=new File(basePath+"_"+suffix+".jpg");
			if (file.exists())
			{
				Dimension size=PictureUtils.getImageSize(file);
				if (size!=null)
				{
					if (size.width==50 && size.height==50)
					{
						map.put(Picture.THUMBNAIL_50x50, new ImageData(file, size));
					}
					else if (size.width<=170)
					{
						ImageData currentData=map.get(Picture.THUMBNAIL_SIDEBAR);
						if (currentData==null || currentData.getSize().width<size.width)
						{
							map.put(Picture.THUMBNAIL_SIDEBAR, new ImageData(file, size));
						}
					}
				}
			}
		}
		return map;
	}

}
