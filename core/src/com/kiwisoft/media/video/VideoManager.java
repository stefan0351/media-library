/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media.video;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;

public class VideoManager
{
	public static final int GROUP_SIZE=50;

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

	public Collection<Video> getAllVideos()
	{
		return DBLoader.getInstance().loadSet(Video.class);
	}

	public Collection<Video> getVideos(MediumType type)
	{
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

	public int getGroupCount()
	{
		try
		{
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select max((cast(substr(userkey, 2) as signed)-1) div ?) from videos ");
			try
			{
				statement.setInt(1, GROUP_SIZE);
				ResultSet resultSet=statement.executeQuery();
				if (resultSet.next()) return resultSet.getInt(1)+1;
				return 1;
			}
			finally
			{
				statement.close();
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Set<Video> getGroupVideos(int group)
	{
		return DBLoader.getInstance().loadSet(Video.class, null, "(cast(substr(userkey, 2) as signed)-1) div ?=?", GROUP_SIZE, group);
	}

	public static String getGroupName(int group)
	{
		return "D"+(group*50+1)+"-D"+(group*50+50);
	}

	public Set<Video> getVideos(Movie movie)
	{
		return DBLoader.getInstance().loadSet(Video.class, "recordings", "recordings.video_id=videos.id" +
																		 " and recordings.movie_id=?", movie.getId());
	}

	public Set<Video> getVideos(Episode episode)
	{
		return DBLoader.getInstance().loadSet(Video.class, "recordings", "recordings.video_id=videos.id" +
																		 " and recordings.episode_id=?", episode.getId());
	}
}
