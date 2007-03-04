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

import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.DateUtils;

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

	public Set<Airdate> getAirdates(int unit, int quantity)
	{
		Date now=new Date();
		Date startTime=DateUtils.add(now, Calendar.HOUR, -2);
		System.out.println("startTime = "+startTime);
		Date endTime=DateUtils.add(now, unit, quantity);
		System.out.println("endTime = "+endTime);
		return DBLoader.getInstance().loadSet(Airdate.class, null, "viewdate>=? and viewdate<?", startTime, endTime);
	}

	public Set<Airdate> getAirdatesToday()
	{
		return DBLoader.getInstance().loadSet(Airdate.class, null, "to_days(viewdate)=to_days(current_date)");
	}
}

