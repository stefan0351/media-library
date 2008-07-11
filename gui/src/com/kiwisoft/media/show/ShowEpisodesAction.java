package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowEpisodesAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowEpisodesAction(ApplicationFrame frame)
	{
		super(Show.class, "Episodes");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new EpisodesView((Show)getObject()));
	}
}
