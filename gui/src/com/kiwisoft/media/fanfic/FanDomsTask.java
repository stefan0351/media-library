package com.kiwisoft.media.fanfic;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 22:00:03
 * To change this template use File | Settings | File Templates.
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
