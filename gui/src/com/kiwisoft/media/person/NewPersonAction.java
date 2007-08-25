package com.kiwisoft.media.person;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class NewPersonAction extends ContextAction<Person>
{
	public NewPersonAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PersonDetailsView.create(null, true);
	}
}
