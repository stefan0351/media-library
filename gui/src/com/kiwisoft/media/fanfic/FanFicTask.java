package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class FanFicTask extends MenuSidebarItem.Task
{
	public FanFicTask()
	{
		super("Fan Fiction");
		add(new FanFicAuthorsTask());
		add(new FanFicPairingsTask());
		add(new FanDomsTask());
	}
}
