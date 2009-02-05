package com.kiwisoft.media.books;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class BooksTask extends MenuSidebarItem.Task
{
	private static Log log=LogFactory.getLog(BooksTask.class);

	public BooksTask()
	{
		super("Books");
	}

	public void perform(ApplicationFrame frame)
	{
		log.debug("Books Task selected");
		frame.setCurrentView(new BooksView());
	}
}
