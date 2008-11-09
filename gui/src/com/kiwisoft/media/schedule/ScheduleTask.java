package com.kiwisoft.media.schedule;

import java.util.Calendar;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class ScheduleTask extends MenuSidebarItem.Task
{
	public ScheduleTask()
	{
		super("TV Schedule");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ScheduleView());
	}
}
