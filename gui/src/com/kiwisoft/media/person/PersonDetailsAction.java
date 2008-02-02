package com.kiwisoft.media.person;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class PersonDetailsAction extends SimpleContextAction
{
	public PersonDetailsAction()
	{
		super(Person.class, "Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PersonDetailsView.create((Person)getObject(), true);
	}
}
