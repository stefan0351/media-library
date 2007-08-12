package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class FanDomsTask extends MenuSidebarItem.Task
{
	public FanDomsTask()
	{
		super("Domains");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new FanDomsView(), true);
	}
}
