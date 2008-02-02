package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowSeasonsAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowSeasonsAction(ApplicationFrame frame)
	{
		super(Show.class, "Seasons");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new SeasonsView((Show)getObject()), true);
	}
}
