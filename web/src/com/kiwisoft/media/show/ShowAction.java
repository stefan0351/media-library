package com.kiwisoft.media.show;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.files.*;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

import java.util.TreeSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 05.10.2009
 */
public class ShowAction extends BaseAction
{
	private Long showId;
	private Show show;
	private Set<Season> seasons;

	@Override
	public String getPageTitle()
	{
		return show.getTitle();
	}

	@Override
	public String execute() throws Exception
	{
		if (showId!=null) setShow(ShowManager.getInstance().getShow(showId));
		return SUCCESS;
	}

	protected void setShow(Show show)
	{
		this.show=show;
		if (show!=null)
		{
			RecentItemManager.getInstance().addItem(new RecentIdObject<Show>(Show.class, show));
			seasons=new TreeSet<Season>(show.getSeasons());
		}
	}

	public Show getShow()
	{
		return show;
	}

	public Set<Season> getSeasons()
	{
		return seasons;
	}

	public Long getShowId()
	{
		return showId;
	}

	public void setShowId(Long showId)
	{
		this.showId=showId;
	}

	public MediaFile findLogo()
	{
		if (getShow()!=null) return getShow().getLogo();
		return null;
	}

	public Episode getEpisode(String key)
	{
		return ShowManager.getInstance().getEpisode(show.getUserKey(), key);
	}
}
