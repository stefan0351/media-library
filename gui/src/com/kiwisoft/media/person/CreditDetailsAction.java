package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class CreditDetailsAction extends SimpleContextAction
{
	public CreditDetailsAction()
	{
		super(Credit.class, "Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		CreditDetailsView.create((Credit)getObject());
	}
}
