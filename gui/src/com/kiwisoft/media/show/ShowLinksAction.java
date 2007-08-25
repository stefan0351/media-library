package com.kiwisoft.media.show;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.LinksView;
import com.kiwisoft.app.ApplicationFrame;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class ShowLinksAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowLinksAction(ApplicationFrame frame)
	{
		super("Links", Icons.getIcon("links"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new LinksView(getObject()), true);
	}
}
