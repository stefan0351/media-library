/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 24, 2003
 * Time: 10:42:43 PM
 */
package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

public class FanFicsAction<T extends FanFicGroup> extends SimpleContextAction<T>
{
	private ApplicationFrame frame;

	public FanFicsAction(ApplicationFrame frame)
	{
		super("Fan Fiction");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new FanFicsView(getObject()), true);
	}
}
