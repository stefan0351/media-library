package com.kiwisoft.media;

import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.media.pics.PicturesTask;

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
