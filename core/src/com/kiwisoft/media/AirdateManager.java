/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 31, 2003
 * Time: 7:27:21 PM
 * To change this template use Options | File Templates.
 */
package com.kiwisoft.media;

import java.util.Date;
import java.util.Set;
import java.util.Calendar;
import java.util.Collections;

import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.media.show.Show;

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

	public Set<Airdate> getAirdates(Show show, Date startDate, Date endDate)
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "show_id=? and viewdate between ? and ?", show.getId(), startDate, endDate);
	}
}

