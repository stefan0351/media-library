/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media.fanfic;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeSupport;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.DBSession;

public class FanFicManager
{
	private static FanFicManager instance;

	public static final String PAIRINGS="pairings";
	public static final String AUTHORS="authors";
	public static final String DOMAINS="domains";
	public static final String FANFICS="fanFics";

	private CollectionChangeSupport collectionChangeSupport=new CollectionChangeSupport(this);

	public synchronized static FanFicManager getInstance()
	{
		if (instance==null) instance=new FanFicManager();
		return instance;
	}

	private FanFicManager()
	{
	}

	public Set<Pairing> getPairings()
	{
		return DBLoader.getInstance().loadSet(Pairing.class);
	}

	public Pairing createPairing()
	{
		Pairing pairing=new Pairing();
		fireElementAdded(PAIRINGS, pairing);
		return pairing;
	}

	public void dropPairing(Pairing pairing)
	{
		pairing.delete();
		fireElementRemoved(PAIRINGS, pairing);
	}

	public Pairing getPairing(Long id)
	{
		return DBLoader.getInstance().load(Pairing.class, id);
	}

	public Set<Author> getAuthors()
	{
		return DBLoader.getInstance().loadSet(Author.class);
	}

	public Set<Author> getAuthors(char ch)
	{
		return DBLoader.getInstance().loadSet(Author.class, null, "name like ?", ch+"%");
	}

	public SortedSet<Character> getAuthorLetters()
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct upper(left(name, 1)) from fanficauthors");
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

	public Set<FanFic> getFanFics(Author author, char ch)
	{
		return DBLoader.getInstance().loadSet(FanFic.class, "map_fanfic_author m",
				"m.fanfic_id=fanfics.id and fanfics.title like ? and m.author_id=?", ch+"%", author.getId());
	}

	public SortedSet<Character> getFanFicLetters(Author author)
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct upper(left(title, 1))" +
					" from fanfics, map_fanfic_author m" +
					" where fanfics.id=m.fanfic_id and author_id=?");
			statement.setLong(1, author.getId().longValue());
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

	public Set<FanFic> getFanFics(Pairing pairing, char ch)
	{
		return DBLoader.getInstance().loadSet(FanFic.class, "map_fanfic_pairing m",
				"m.fanfic_id=fanfics.id and fanfics.title like ? and m.pairing_id=?", ch+"%", pairing.getId());
	}

	public SortedSet<Character> getFanFicLetters(Pairing pairing)
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct upper(left(title, 1))" +
					" from fanfics, map_fanfic_pairing m" +
					" where fanfics.id=m.fanfic_id and pairing_id=?");
			statement.setLong(1, pairing.getId().longValue());
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

	public Set<FanFic> getFanFics(FanDom fanDom, char ch)
	{
		return DBLoader.getInstance().loadSet(FanFic.class, "map_fanfic_fandom m",
				"m.fanfic_id=fanfics.id and fanfics.title like ? and m.fandom_id=?", ch+"%", fanDom.getId());
	}

	public SortedSet<Character> getFanFicLetters(FanDom fanDom)
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct upper(left(title, 1))" +
					" from fanfics, map_fanfic_fandom m" +
					" where fanfics.id=m.fanfic_id and fandom_id=?");
			statement.setLong(1, fanDom.getId().longValue());
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

	public Set<FanFic> getFanFics(Show show, char ch)
	{
		return DBLoader.getInstance().loadSet(FanFic.class, "map_fanfic_fandom m, fandoms f",
				"m.fanfic_id=fanfics.id and m.fandom_id=f.id and fanfics.title like ? and f.show_id=?", ch+"%", show.getId());
	}

	public SortedSet<Character> getFanFicLetters(Show show)
	{
		try
		{
			SortedSet<Character> set=new TreeSet<Character>();
			Connection connection=DBSession.getInstance().getConnection();
			PreparedStatement statement=connection.prepareStatement("select distinct upper(left(title, 1))" +
					" from fanfics, map_fanfic_fandom m, fandoms f" +
					" where fanfics.id=m.fanfic_id and f.id=m.fandom_id and f.show_id=?");
			statement.setLong(1, show.getId().longValue());
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

	public Author createAuthor()
	{
		Author author=new Author();
		fireElementAdded(AUTHORS, author);
		return author;
	}

	public void dropAuthor(Author author)
	{
		author.delete();
		fireElementRemoved(AUTHORS, author);
	}

	public boolean isAuthorUsed(Author author)
	{
		return false;
	}

	public Author getAuthor(Long id)
	{
		return DBLoader.getInstance().load(Author.class, id);
	}

	public Author getAuthor(String key)
	{
		return DBLoader.getInstance().load(Author.class, null, "path=?", key);
	}

	public Set<FanDom> getDomains()
	{
		return DBLoader.getInstance().loadSet(FanDom.class);
	}

	public Set<FanDom> getShowDomains()
	{
		return DBLoader.getInstance().loadSet(FanDom.class, null, "show_id is not null");
	}

	public Set<FanDom> getMovieDomains()
	{
		return DBLoader.getInstance().loadSet(FanDom.class, null, "movie_id is not null");
	}

	public Set<FanDom> getOtherDomains()
	{
		return DBLoader.getInstance().loadSet(FanDom.class, null, "movie_id is null and show_id is null");
	}

	public FanDom getDomain(Long id)
	{
		return DBLoader.getInstance().load(FanDom.class, id);
	}

	public FanDom createDomain()
	{
		FanDom domain=new FanDom();
		fireElementAdded(DOMAINS, domain);
		return domain;
	}

	public void dropDomain(FanDom domain)
	{
		domain.delete();
		fireElementRemoved(DOMAINS, domain);
	}

	public FanFic getFanFic(Long id)
	{
		return DBLoader.getInstance().load(FanFic.class, id);
	}

	public FanFic createFanFic()
	{
		FanFic fanFic=new FanFic();
		fireElementAdded(FANFICS, fanFic);
		return fanFic;
	}

	public void dropFanFic(FanFic fanFic)
	{
		fanFic.delete();
		fireElementRemoved(FANFICS, fanFic);
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

	public void notifyFanFicChanged(FanFic fanFic)
	{
		collectionChangeSupport.fireElementChanged(FANFICS, fanFic);
	}
}

