package com.kiwisoft.media;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class ConfigurationTask extends MenuSidebarItem.Task
{
	public ConfigurationTask()
	{
		super("Configuration");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ConfigurationView());
	}
}
