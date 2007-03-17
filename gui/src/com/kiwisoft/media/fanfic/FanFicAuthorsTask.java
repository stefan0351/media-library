package com.kiwisoft.media.fanfic;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:59:21
 * To change this template use File | Settings | File Templates.
 */
public class FanFicAuthorsTask extends MenuSidebarItem.Task
{
	public FanFicAuthorsTask()
	{
		super("Authors");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new AuthorsView(), true);
	}
}
