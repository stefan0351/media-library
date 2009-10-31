package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class TracksAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public TracksAction(ApplicationFrame frame)
	{
		super(Medium.class, "Tracks", Icons.getIcon("tracks"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new TracksView((Medium)getObject()));
	}
}
