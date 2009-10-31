package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.Airdate;

/**
 * @author Stefan Stiller
 */
public class AirdateDetailsAction extends SimpleContextAction
{
	protected AirdateDetailsAction()
	{
		super(Airdate.class, "Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		AirdateDetailsView.create((Airdate)getObject());
	}
}
