/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.util.*;

import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.person.Person;

public class AirdateManager
{
	private static AirdateManager instance;

	public synchronized static AirdateManager getInstance()
	{
		if (instance==null) instance=new AirdateManager();
		return instance;
	}

	private AirdateManager()
	{
	}

	public Set<Airdate> getOtherAirdates()
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "show_id is null");
	}

	public Set<Airdate> getAirdates(Date startDate, Date endDate)
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "viewdate between ? and ?", startDate, endDate);
	}

	public Set<Airdate> getAirdates(Channel channel, Date startDate)
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "channel_id=? and viewdate=?", channel.getId(), startDate);
	}

	public Set<Airdate> getAirdates(Show show, Date startDate, Date endDate)
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "show_id=? and viewdate between ? and ?", show.getId(), startDate, endDate);
	}

	public Set<Airdate> getAirdates(Person person, Date startDate, Date endDate)
	{
		Set<Airdate> airdates=new HashSet<Airdate>();
		airdates.addAll(DBLoader.getInstance().loadSet(Airdate.class, "_ join cast on cast.show_id=airdates.show_id", "cast.actor_id=? and viewdate between ? and ?",
													   person.getId(), startDate, endDate));
		airdates.addAll(DBLoader.getInstance().loadSet(Airdate.class, "_ join cast on cast.episode_id=airdates.episode_id", "cast.actor_id=? and viewdate between ? and ?",
													   person.getId(), startDate, endDate));
		airdates.addAll(DBLoader.getInstance().loadSet(Airdate.class, "_ join cast on cast.movie_id=airdates.movie_id", "cast.actor_id=? and viewdate between ? and ?",
													   person.getId(), startDate, endDate));
		airdates.addAll(DBLoader.getInstance().loadSet(Airdate.class, "_ join airdate_persons m on m.airdate_id=airdates.id", "m.person_id=? and viewdate between ? and ?",
													   person.getId(), startDate, endDate));
		return airdates;
	}
}

