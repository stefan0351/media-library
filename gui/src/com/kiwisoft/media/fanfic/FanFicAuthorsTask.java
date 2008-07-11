package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class FanFicAuthorsTask extends MenuSidebarItem.Task
{
	public FanFicAuthorsTask()
	{
		super("Authors");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new AuthorsView());
	}
}
