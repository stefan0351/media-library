package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewSeasonAction extends ContextAction
{
	private Show show;

	public NewSeasonAction(Show show)
	{
		super("New", Icons.getIcon("add"));
		this.show=show;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		SeasonDetailsView.create(show);
	}
}
