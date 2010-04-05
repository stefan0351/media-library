package com.kiwisoft.media;

import com.kiwisoft.web.RecentItem;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.utils.DateUtils;

import java.util.*;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class HomeAction extends BaseAction
{
	private List<Object> recentItems;
	private List<Airdate> airdates;

	@Override
	public String execute() throws Exception
	{
		recentItems=new ArrayList<Object>();
		List<RecentItem> recentItemList=RecentItemManager.getInstance().getRecentItems();
		for (RecentItem recentItem : recentItemList)
		{
			Object item=recentItem.getItem();
			if (item!=null) recentItems.add(item);
		}
		Date now=new Date();
		airdates=new ArrayList<Airdate>(AirdateManager.getInstance().getAirdates(now, DateUtils.add(now, Calendar.HOUR, 12)));
		Collections.sort(airdates, new AirdateComparator(AirdateComparator.INV_TIME));
		return super.execute();
	}

	public List<Object> getRecentItems()
	{
		return recentItems;
	}

	public List<Airdate> getAirdates()
	{
		return airdates;
	}
}
