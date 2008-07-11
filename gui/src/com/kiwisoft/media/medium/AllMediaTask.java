package com.kiwisoft.media.medium;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class AllMediaTask extends MenuSidebarItem.Task
{
	public AllMediaTask()
	{
		super("Media");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new MediaView());
	}
}
