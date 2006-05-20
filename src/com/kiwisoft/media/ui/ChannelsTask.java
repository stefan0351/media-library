package com.kiwisoft.media.ui;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

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
		super("Sender", IconManager.getIcon("com/kiwisoft/media/icons/channel32.gif"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ChannelsView(), true);
	}
}
