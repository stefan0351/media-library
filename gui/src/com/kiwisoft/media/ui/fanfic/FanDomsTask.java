package com.kiwisoft.media.ui.fanfic;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.ui.fanfic.FanDomsView;

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
		super("Domänen", IconManager.getIcon("com/kiwisoft/media/icons/fanfic16.gif"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new FanDomsView(), true);
	}
}
