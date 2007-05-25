package com.kiwisoft.media.photos;

import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class PhotosTask extends MenuSidebarItem.Task
{
	public PhotosTask()
	{
		super("Photos");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PhotoGalleriesView(), true);
	}
}
