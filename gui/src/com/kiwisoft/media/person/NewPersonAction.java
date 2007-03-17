package com.kiwisoft.media.person;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 04.03.2007
 * Time: 11:25:39
 * To change this template use File | Settings | File Templates.
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
