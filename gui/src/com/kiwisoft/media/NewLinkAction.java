package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.media.show.Show;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 11:13:53
 * To change this template use File | Settings | File Templates.
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
