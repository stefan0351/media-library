package com.kiwisoft.media;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.web.RecentIdObject;
import com.kiwisoft.web.RecentItem;
import com.kiwisoft.web.RecentItemManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class HomeAction extends BaseAction
{
	private List<Show> shows;

	@Override
	public String execute() throws Exception
	{
		shows=new ArrayList<Show>();
		List<RecentItem> recentItemList=RecentItemManager.getInstance().getRecentItems(Show.class.getName());
		for (RecentItem recentItem : recentItemList)
		{
			shows.add((Show) ((RecentIdObject) recentItem).getObject());
		}
		return super.execute();
	}

	public List<Show> getShows()
	{
		return shows;
	}
}
