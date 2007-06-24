package com.kiwisoft.media;

import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class ConfigurationTask extends MenuSidebarItem.Task
{
	public ConfigurationTask()
	{
		super("Configuration");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ConfigurationView(), true);
	}
}
