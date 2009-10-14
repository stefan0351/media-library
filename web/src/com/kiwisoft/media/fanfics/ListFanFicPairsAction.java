package com.kiwisoft.media.fanfics;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.media.fanfic.FanFicManager;
import com.kiwisoft.media.fanfic.FanDom;
import com.kiwisoft.media.fanfic.Pairing;

import java.util.TreeSet;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 03.10.2009
 */
public class ListFanFicPairsAction extends BaseAction
{
	private Set<Pairing> pairings;

	@Override
	public String getPageTitle()
	{
		return "Fan Fiction - Pairings";
	}

	@Override
	public String execute() throws Exception
	{
		pairings=new TreeSet<Pairing>(FanFicManager.getInstance().getPairings());
		return super.execute();
	}

	public Set<Pairing> getPairings()
	{
		return pairings;
	}
}