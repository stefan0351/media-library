package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class EpisodeDetailsAction extends SimpleContextAction
{
	public EpisodeDetailsAction()
	{
		super(Episode.class, "Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create((Episode)getObject());
	}
}
