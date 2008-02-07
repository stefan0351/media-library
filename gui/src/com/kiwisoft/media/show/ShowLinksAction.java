package com.kiwisoft.media.show;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.links.LinksView;
import com.kiwisoft.media.LinkGroup;
import com.kiwisoft.media.Linkable;
import com.kiwisoft.app.ApplicationFrame;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class ShowLinksAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowLinksAction(ApplicationFrame frame)
	{
		super(Linkable.class, "Links", Icons.getIcon("links"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new LinksView((Linkable)getObject()), true);
	}
}
