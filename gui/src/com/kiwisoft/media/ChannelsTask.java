package com.kiwisoft.media;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * @author Stefan Stiller
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
