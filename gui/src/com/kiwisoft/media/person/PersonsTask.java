package com.kiwisoft.media.person;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class PersonsTask extends MenuSidebarItem.Task
{
	public PersonsTask()
	{
		super("Persons");
	}

	@Override
	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PersonsView());
	}
}
