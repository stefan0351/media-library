package com.kiwisoft.media;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:57:02
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsTask extends MenuSidebarItem.Task
{
	public ChannelsTask()
	{
		super("Channels");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView(), true);
	}
}
