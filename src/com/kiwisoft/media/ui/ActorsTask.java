package com.kiwisoft.media.ui;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:57:14
 * To change this template use File | Settings | File Templates.
 */
public class ActorsTask extends MenuSidebarItem.Task
{
	public ActorsTask()
	{
		super("Darsteller", IconManager.getIcon("com/kiwisoft/media/icons/actors32.jpg"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ActorsView(), true);
	}
}
