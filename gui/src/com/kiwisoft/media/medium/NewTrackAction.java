package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewTrackAction extends ContextAction<Track>
{
	private Medium medium;

	public NewTrackAction(Medium medium)
	{
		super("New", Icons.getIcon("add"));
		this.medium=medium;
	}

	public void actionPerformed(ActionEvent e)
	{
		TrackDetailsView.create(medium);
	}
}
