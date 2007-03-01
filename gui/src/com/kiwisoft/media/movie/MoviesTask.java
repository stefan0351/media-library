package com.kiwisoft.media.movie;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

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
		super("Filme", IconManager.getIcon("com/kiwisoft/media/icons/movies32.gif"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new MoviesView(null), true);
	}
}
