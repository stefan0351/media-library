package com.kiwisoft.media.person;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class NewPersonAction extends ContextAction
{
	public NewPersonAction()
	{
		super("New", Icons.getIcon("add"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PersonDetailsView.create(null);
	}
}
