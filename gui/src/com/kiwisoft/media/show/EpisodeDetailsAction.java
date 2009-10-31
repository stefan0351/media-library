package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.awt.*;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class EpisodeDetailsAction extends SimpleContextAction
{
	private Window window;

	public EpisodeDetailsAction(Window window)
	{
		super(Episode.class, "Details", Icons.getIcon("details"));
		this.window=window;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(window, (Episode)getObject());
	}
}
