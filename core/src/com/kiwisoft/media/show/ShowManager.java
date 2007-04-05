/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 12, 2003
 * Time: 1:08:16 PM
 */
package com.kiwisoft.media.show;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeSupport;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.media.video.Recording;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.Airdate;

public class ShowManager
{
	public static final String SHOWS="shows";

	private static ShowManager instance;

	public synchronized static ShowManager getInstance()
	{
		if (instance==null) instance=new ShowManager();
		return instance;
	}

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	private ShowManager()
	{
	}

	public Collection<Show> getShows()
	{
		return DBLoader.getInstance().loadSet(Show.class);
	}

	public int getShowCount()
	{
		return DBLoader.getInstance().count(Show.class);
	}

	public Show getShowByName(String name)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		Show show=dbLoader.load(Show.class, null, "title=? or german_title=?", name, name);
		if (show==null)
		{
			show=dbLoader.load(Show.class, "names", "names.type=? and names.ref_id=shows.id and names.name=?", Name.SHOW, name);
		}
		return show;
	}

	public Episode getEpisodeByName(Show show, String name)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		Episode episode=dbLoader.load(Episode.class, null, "show_id=? and (title=? or german_title=?)", show.getId(), name, name);
		if (episode==null)
		{
			episode=dbLoader.load(Episode.class, "names",
					"names.type=? and show_id=? and names.ref_id=episodes.id and names.name=?",
					Name.EPISODE, show.getId(), name);
		}
		return episode;
	}

	public boolean isShowUsed(Show show)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		return (dbLoader.count(Recording.class, null, "show_id=?", show.getId())>0)
		        || (dbLoader.count(Airdate.class, null, "show_id=?", show.getId())>0);
	}

	public boolean isEpisodeUsed(Episode episode)
	{
		DBLoader dbLoader=DBLoader.getInstance();
		return (dbLoader.count(Recording.class, null, "episode_id=?", episode.getId())>0)
				|| (dbLoader.count(Airdate.class, null, "episode_id=?", episode.getId())>0);
	}

	public Set getInternetShows()
	{
		return DBLoader.getInstance().loadSet(Show.class, null, "internet=?", true);
	}

	public Show createShow()
	{
		Show show=new Show();
		fireElementAdded(SHOWS, show);
		return show;
	}

	public void dropShow(Show show)
	{
		show.delete();
		fireElementRemoved(SHOWS, show);
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

	public Show getShow(Long showId)
	{
		return DBLoader.getInstance().load(Show.class, showId);
	}

	public Episode getEpisode(Long episodeId)
	{
		return DBLoader.getInstance().load(Episode.class, episodeId);
	}

	public Episode getEpisode(String showKey, String episodeKey)
	{
		return DBLoader.getInstance().load(Episode.class, "shows", "shows.id=episodes.show_id and shows.userkey=? and episodes.userkey=?", showKey, episodeKey);
	}

	public Show getShow(String showKey)
	{
		return DBLoader.getInstance().load(Show.class, null, "userkey=?", showKey);
	}

	public Season getSeason(Long id)
	{
		return DBLoader.getInstance().load(Season.class, id);
	}

	public SortedSet<Character> getLetters()
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct sort_letter(index_by) from shows");
			try
			{
				ResultSet resultSet=statement.executeQuery();
				while (resultSet.next())
				{
					String string=resultSet.getString(1);
					if (string!=null && string.length()>0)
						set.add(new Character(string.charAt(0)));
				}
			}
			finally
			{
				statement.close();
			}
			return set;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Set<Show> getShowsByLetter(char ch)
	{
		return DBLoader.getInstance().loadSet(Show.class, null, "sort_letter(index_by)=?", String.valueOf(ch));
	}
}
