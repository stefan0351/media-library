package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class ScheduleUpdateManagerAction extends ContextAction
{
	private ApplicationFrame frame;

	public ScheduleUpdateManagerAction(ApplicationFrame frame)
	{
		super("Update Manager");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ScheduleUpdateManagerView());
	}
}
