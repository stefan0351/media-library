package com.kiwisoft.media.photos;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class PhotosTask extends MenuSidebarItem.Task
{
	public PhotosTask()
	{
		super("Photos");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PhotoGalleriesView());
	}
}
