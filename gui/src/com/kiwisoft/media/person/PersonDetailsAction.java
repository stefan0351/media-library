package com.kiwisoft.media.person;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:18
 * To change this template use File | Settings | File Templates.
 */
public class PersonDetailsAction extends SimpleContextAction<Person>
{
	public PersonDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PersonDetailsView.create(getObject(), true);
	}
}
