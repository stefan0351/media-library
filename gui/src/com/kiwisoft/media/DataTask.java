package com.kiwisoft.media;

import com.kiwisoft.media.pics.PicturesTask;
import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class DataTask extends MenuSidebarItem.Task
{
	public DataTask()
	{
		super("Data");
		add(new ChannelsTask());
		add(new ConfigurationTask());
		add(new PicturesTask());
	}
}
