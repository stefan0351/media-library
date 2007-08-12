package com.kiwisoft.media.pics;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class PicturesTask extends MenuSidebarItem.Task
{
	public PicturesTask()
	{
		super("Pictures");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PicturesView(), true);
	}
}
