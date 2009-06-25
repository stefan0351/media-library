package com.kiwisoft.media.movie;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class MoviesTask extends MenuSidebarItem.Task
{
	public MoviesTask()
	{
		super("Movies");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new MoviesView(null));
	}
}
