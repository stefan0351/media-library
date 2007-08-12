package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class TrackDetailsAction extends SimpleContextAction<Track>
{
	public TrackDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		TrackDetailsView.create(getObject());
	}
}
