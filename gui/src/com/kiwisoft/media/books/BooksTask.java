package com.kiwisoft.media.books;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

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
