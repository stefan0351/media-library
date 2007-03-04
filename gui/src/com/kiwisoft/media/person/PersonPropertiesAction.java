package com.kiwisoft.media.person;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:18
 * To change this template use File | Settings | File Templates.
 */
public class PersonPropertiesAction extends SimpleContextAction<Person>
{
	public PersonPropertiesAction()
	{
		super("Eigenschaften");
	}

	public void actionPerformed(ActionEvent e)
	{
		PersonDetailsView.create(getObject(), true);
	}
}
