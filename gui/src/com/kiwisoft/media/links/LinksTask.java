package com.kiwisoft.media.links;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.links.LinksView;

/**
 * @author Stefan Stiller
 */
public class LinksTask extends MenuSidebarItem.Task
{
	public LinksTask()
	{
		super("Links");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new LinksView());
	}
}
