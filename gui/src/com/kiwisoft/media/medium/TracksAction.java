package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class TracksAction extends SimpleContextAction<Medium>
{
	private ApplicationFrame frame;

	public TracksAction(ApplicationFrame frame)
	{
		super("Tracks", Icons.getIcon("tracks"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new TracksView(getObject()), true);
	}
}
