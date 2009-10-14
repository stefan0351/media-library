package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanDom;

import java.util.TreeSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListFanDomsAction extends BaseAction
{
	private Set<FanDom> showDomains;
	private Set<FanDom> movieDomains;
	private Set<FanDom> otherDomains;

	@Override
	public String getPageTitle()
	{
		return "Fan Fiction - Domains";
	}

	@Override
	public String execute() throws Exception
	{
		showDomains=new TreeSet<FanDom>(FanFicManager.getInstance().getShowDomains());
		movieDomains=new TreeSet<FanDom>(FanFicManager.getInstance().getMovieDomains());
		otherDomains=new TreeSet<FanDom>(FanFicManager.getInstance().getOtherDomains());
		return super.execute();
	}

	public Set<FanDom> getShowDomains()
	{
		return showDomains;
	}

	public Set<FanDom> getMovieDomains()
	{
		return movieDomains;
	}

	public Set<FanDom> getOtherDomains()
	{
		return otherDomains;
	}
}
