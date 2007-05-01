package com.kiwisoft.media.person;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * @author Stefan Stiller
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
