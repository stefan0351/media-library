package com.kiwisoft.media.pics;

import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

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
