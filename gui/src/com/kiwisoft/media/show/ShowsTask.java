package com.kiwisoft.media.show;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class ShowsTask extends MenuSidebarItem.Task
{
	public ShowsTask()
	{
		super("Shows");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ShowSearchView());
	}
}
