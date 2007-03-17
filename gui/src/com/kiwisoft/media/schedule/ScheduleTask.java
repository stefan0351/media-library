package com.kiwisoft.media.schedule;

import java.util.Calendar;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:55:45
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleTask extends MenuSidebarItem.Task
{
	public ScheduleTask()
	{
		super("Schedule");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new ScheduleView(Calendar.DATE, 1), true);
	}
}
