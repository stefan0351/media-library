package com.kiwisoft.media.books;

import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.video.VideosView;

/**
 * @author Stefan Stiller
 */
public class BooksTask extends MenuSidebarItem.Task
{
	public BooksTask()
	{
		super("Books");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new BooksView(), true);
	}
}
