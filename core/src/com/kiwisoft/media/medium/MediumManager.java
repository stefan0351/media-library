/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media.medium;

import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.utils.Bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MediumManager extends Bean
{
	public static final int GROUP_SIZE=50;

	public static final String MEDIA="media";

	private static MediumManager instance;

	public synchronized static MediumManager getInstance()
	{
		if (instance==null) instance=new MediumManager();
		return instance;
	}

	private MediumManager()
	{
	}

	public Set<Medium> getAllMedia()
	{
		return DBLoader.getInstance().loadSet(Medium.class);
	}

	public Set<Medium> getMedia(MediumType type, boolean includeObsolete)
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

	public void fireElementChanged(Medium video)
	{
		fireElementChanged(MEDIA, video);
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

	public Set<Track> getMovieTracks()
	{
		return DBLoader.getInstance().loadSet(Track.class, "media",
											  "media.id=tracks.medium_id"+
											  " and movie_id is not null and media.userkey is not null"+
											  " and ifnull(media.obsolete, 0)=0");
	}

	public Collection<String> getStorages()
	{
		try
		{
			Set<String> storages=new HashSet<String>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct storage from media");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next()) storages.add(resultSet.getString(1));
			}
			finally
			{
				statement.close();
			}
			return storages;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
