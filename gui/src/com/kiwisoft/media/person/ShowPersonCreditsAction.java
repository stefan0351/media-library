package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.show.Production;

/**
 * @author Stefan Stiller
 */
public class ShowPersonCreditsAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowPersonCreditsAction(ApplicationFrame frame)
	{
		super(Person.class, "Credits", Icons.getIcon("cast"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new PersonCreditsView((Person)getObject()));
	}
}
