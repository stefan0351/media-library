package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.media.Airdate;

/**
 * @author Stefan Stiller
 */
public class AirdateDetailsAction extends SimpleContextAction<Airdate>
{
	protected AirdateDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		AirdateDetailsView.create(getObject());
	}
}
