package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class SeasonDetailsAction extends SimpleContextAction<Season>
{
	protected SeasonDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		SeasonDetailsView.create(getObject());
	}
}
