package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:49:16
 * To change this template use File | Settings | File Templates.
 */
public class ShowRecordingsAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowRecordingsAction(ApplicationFrame frame)
	{
		super("Records", Icons.getIcon("records"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ShowRecordingsView(getObject()), true);
	}
}
