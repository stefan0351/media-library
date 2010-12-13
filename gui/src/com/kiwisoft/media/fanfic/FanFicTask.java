package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

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

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new FanFicsSearchView());
	}
}
