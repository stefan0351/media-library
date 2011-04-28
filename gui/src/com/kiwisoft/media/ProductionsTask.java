package com.kiwisoft.media;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.media.books.BooksTask;
import com.kiwisoft.media.medium.AllMediaTask;
import com.kiwisoft.media.movie.MoviesTask;
import com.kiwisoft.media.person.PersonsTask;
import com.kiwisoft.media.show.ShowsTask;

/**
 * @author Stefan Stiller
 */
public class ProductionsTask extends MenuSidebarItem.Task
{
	public ProductionsTask()
	{
		super("Productions");
		add(new MoviesTask());
		add(new PersonsTask());
		add(new ShowsTask());
	}
}
