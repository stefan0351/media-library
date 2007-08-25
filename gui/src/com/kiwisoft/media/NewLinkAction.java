package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.show.Show;

/**
 * @author Stefan Stiller
 */
public class NewLinkAction extends ContextAction<Link>
{
	private Show show;

	public NewLinkAction(Show show)
	{
		super("New", Icons.getIcon("add"));
		this.show=show;
	}

	public void actionPerformed(ActionEvent e)
	{
		LinkDetailsView.create(show);
	}
}
