/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media.medium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.DBSession;

public class MediumManager
{
	public static final int GROUP_SIZE=50;

	public static final String MEDIA="media";

	private static MediumManager instance;

	public synchronized static MediumManager getInstance()
	{
		if (instance==null) instance=new MediumManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private MediumManager()
	{
	}

	public Collection<Medium> getAllMedia()
	{
		return DBLoader.getInstance().loadSet(Medium.class);
	}

	public Collection<Medium> getMedia(MediumType type, boolean includeObsolete)
	{
		return DBLoader.getInstance().loadSet(Medium.class, null,
											  includeObsolete ? "type_id=?" : "type_id=? and ifnull(obsolete, 0)=0",
											  type.getId());
	}

	public Medium getMedium(Long id)
	{
		return DBLoader.getInstance().load(Medium.class, id);
	}

	public int getMediumCount()
	{
		return DBLoader.getInstance().count(Medium.class);
	}

	public Medium createMedium()
	{
		Medium medium=new Medium();
		fireElementAdded(MEDIA, medium);
		return medium;
	}

	public void dropMedium(Medium medium)
	{
		medium.delete();
		fireElementRemoved(MEDIA, medium);
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

	public void fireElementChanged(Medium video)
	{
		collectionChangeSupport.fireElementChanged(MEDIA, video);
	}

	public int getGroupCount()
	{
		try
		{
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select max(userkey) div ? from media ");
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

	public Set<Medium> getGroupMedia(int group)
	{
		return DBLoader.getInstance().loadSet(Medium.class, null, "userkey div ?=?", GROUP_SIZE, group);
	}

	public static String getGroupName(int group)
	{
		return Math.max(1, group*50)+"-"+(group*50+49);
	}

	public Set<Medium> getMedia(Movie movie)
	{
		return DBLoader.getInstance().loadSet(Medium.class, "tracks", "tracks.medium_id=media.id"+
																		 " and tracks.movie_id=?", movie.getId());
	}

	public Set<Medium> getMedia(Episode episode)
	{
		return DBLoader.getInstance().loadSet(Medium.class, "tracks", "tracks.medium_id=media.id"+
																		 " and tracks.episode_id=?", episode.getId());
	}
}
