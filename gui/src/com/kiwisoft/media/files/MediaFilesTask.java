package com.kiwisoft.media.files;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class MediaFilesTask extends MenuSidebarItem.Task
{
	public MediaFilesTask()
	{
		super("Media Files");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new MediaFilesView());
	}
}
