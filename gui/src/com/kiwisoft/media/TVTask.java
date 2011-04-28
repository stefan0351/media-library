package com.kiwisoft.media;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.media.channel.ChannelsTask;
import com.kiwisoft.media.files.MediaFilesTask;
import com.kiwisoft.media.schedule.ScheduleTask;

/**
 * @author Stefan Stiller
 */
public class TVTask extends MenuSidebarItem.Task
{
	public TVTask()
	{
		super("TV");
		add(new ChannelsTask());
		add(new ScheduleTask());
	}
}
