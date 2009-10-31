package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.schedule.ScheduleView;

/**
 * @author Stefan Stiller
 */
public class PersonAirdatesAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public PersonAirdatesAction(ApplicationFrame frame)
	{
		super(Person.class, "Schedule", Icons.getIcon("schedule"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ScheduleView((Person)getObject()));
	}
}
