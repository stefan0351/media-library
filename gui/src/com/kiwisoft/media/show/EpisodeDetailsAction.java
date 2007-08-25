package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class EpisodeDetailsAction extends SimpleContextAction<Episode>
{
	public EpisodeDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(getObject());
	}
}
