package com.kiwisoft.media.movie;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:54:33
 * To change this template use File | Settings | File Templates.
 */
public class MoviesTask extends MenuSidebarItem.Task
{
	public MoviesTask()
	{
		super("Movies");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new MoviesView(null), true);
	}
}
