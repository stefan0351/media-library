package com.kiwisoft.media.movie;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * @author Stefan Stiller
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
