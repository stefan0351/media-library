/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media.video;

import java.util.Collection;
import java.util.regex.Pattern;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;

public class VideoManager
{
	public static final String VIDEOS="videos";

	private static VideoManager instance;
	private static Pattern keyPattern;

	public synchronized static VideoManager getInstance()
	{
		if (instance==null) instance=new VideoManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private VideoManager()
	{
	}

	public Collection<Video> getVideos(MediumType type)
	{
		if (type==null) return DBLoader.getInstance().loadSet(Video.class);
		return DBLoader.getInstance().loadSet(Video.class, null, "type_id=?", type.getId());
	}

	public Video getVideo(Long id)
	{
		return DBLoader.getInstance().load(Video.class, id);
	}

	public int getVideoCount()
	{
		return DBLoader.getInstance().count(Video.class, null, null);
	}

	public Video createVideo()
	{
		Video video=new Video();
		fireElementAdded(VIDEOS, video);
		return video;
	}

	public void dropVideo(Video video)
	{
		video.delete();
		fireElementRemoved(VIDEOS, video);
	}

	public void addCollectionChangeListener(CollectionChangeListener listener)
	{
		collectionChangeSupport.addListener(listener);
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

	public void fireElementChanged(Video video)
	{
		collectionChangeSupport.fireElementChanged(VIDEOS, video);
	}

	public Pattern getKeyPattern()
	{
		if (keyPattern==null) keyPattern=Pattern.compile("([A-Z])(\\d+)");
		return keyPattern;
	}
}
