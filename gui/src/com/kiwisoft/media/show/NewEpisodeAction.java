package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class NewEpisodeAction extends ContextAction
{
	private Show show;

	public NewEpisodeAction(Show show)
	{
		super("New", Icons.getIcon("add"));
		this.show=show;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(show);
	}
}
