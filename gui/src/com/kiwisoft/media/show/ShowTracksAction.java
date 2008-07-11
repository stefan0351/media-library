package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowTracksAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowTracksAction(ApplicationFrame frame)
	{
		super(Show.class, "Tracks", Icons.getIcon("tracks"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ShowTracksView((Show)getObject()));
	}
}
