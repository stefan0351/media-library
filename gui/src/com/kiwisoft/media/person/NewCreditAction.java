package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.show.Production;

/**
 * @author Stefan Stiller
 */
public class NewCreditAction extends ContextAction
{
	private Production production;

	public NewCreditAction(Production production)
	{
		super("New", Icons.getIcon("add"));
		this.production=production;
	}

	public void actionPerformed(ActionEvent e)
	{
		CreditDetailsView.create(production);
	}
}
