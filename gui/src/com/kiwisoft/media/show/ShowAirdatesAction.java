package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.schedule.ScheduleView;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowAirdatesAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowAirdatesAction(ApplicationFrame frame)
	{
		super(Show.class, "Schedule", Icons.getIcon("schedule"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ScheduleView((Show)getObject()));
	}
}
