package com.kiwisoft.web;

import com.kiwisoft.collection.ListMap;

import java.util.List;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class RecentItemManager
{
	private static RecentItemManager instance;

	public static RecentItemManager getInstance()
	{
		if (instance==null) instance=new RecentItemManager();
		return instance;
	}

	private ListMap<String, RecentItem> recentItems=new ListMap<String, RecentItem>();

	public void addItem(RecentItem item)
	{
		recentItems.remove(item.getItemClassId(), item);
		recentItems.add(item.getItemClassId(), item);
	}

	public List<RecentItem> getRecentItems(String itemClassId)
	{
		return recentItems.get(itemClassId);
	}
}
