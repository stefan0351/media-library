package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class TrackDetailsAction extends SimpleContextAction
{
	public TrackDetailsAction()
	{
		super(Track.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		TrackDetailsView.create((Track)getObject());
	}
}
