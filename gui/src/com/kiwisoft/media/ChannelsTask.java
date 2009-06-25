package com.kiwisoft.media;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class ChannelsTask extends MenuSidebarItem.Task
{
	public ChannelsTask()
	{
		super("Channels");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView());
	}
}
