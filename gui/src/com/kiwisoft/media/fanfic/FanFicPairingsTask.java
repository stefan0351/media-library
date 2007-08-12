package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class FanFicPairingsTask extends MenuSidebarItem.Task
{
	public FanFicPairingsTask()
	{
		super("Pairings");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PairingsView(), true);
	}
}
