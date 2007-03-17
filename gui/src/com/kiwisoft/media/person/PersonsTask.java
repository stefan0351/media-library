package com.kiwisoft.media.person;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:57:14
 * To change this template use File | Settings | File Templates.
 */
public class PersonsTask extends MenuSidebarItem.Task
{
	public PersonsTask()
	{
		super("Persons");
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PersonsView(), true);
	}
}
