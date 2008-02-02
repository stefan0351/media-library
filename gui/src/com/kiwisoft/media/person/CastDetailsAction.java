package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class CastDetailsAction extends SimpleContextAction
{
	public CastDetailsAction()
	{
		super(CastMember.class, "Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		CastDetailsView.create((CastMember)getObject());
	}
}
