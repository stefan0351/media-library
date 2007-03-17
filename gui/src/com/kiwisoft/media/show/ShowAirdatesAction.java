package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.media.schedule.ScheduleView;
import com.kiwisoft.media.show.Show;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:53:45
 * To change this template use File | Settings | File Templates.
 */
public class ShowAirdatesAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowAirdatesAction(ApplicationFrame frame)
	{
		super("Schedule", Icons.getIcon("schedule"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ScheduleView(getObject()), true);
	}
}
