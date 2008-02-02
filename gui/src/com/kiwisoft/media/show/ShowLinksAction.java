package com.kiwisoft.media.show;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.links.OldLinksView;
import com.kiwisoft.app.ApplicationFrame;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class ShowLinksAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowLinksAction(ApplicationFrame frame)
	{
		super(Show.class, "Links", Icons.getIcon("links"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new OldLinksView((Show)getObject()), true);
	}
}
