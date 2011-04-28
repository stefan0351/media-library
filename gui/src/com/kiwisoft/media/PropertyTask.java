package com.kiwisoft.media;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.media.books.BooksTask;
import com.kiwisoft.media.channel.ChannelsTask;
import com.kiwisoft.media.files.MediaFilesTask;
import com.kiwisoft.media.medium.AllMediaTask;
import com.kiwisoft.media.movie.MoviesTask;
import com.kiwisoft.media.schedule.ScheduleTask;
import com.kiwisoft.media.show.ShowsTask;

/**
 * @author Stefan Stiller
 */
public class PropertyTask extends MenuSidebarItem.Task
{
	public PropertyTask()
	{
		super("Property");
		add(new BooksTask());
		add(new AllMediaTask());
	}
}
