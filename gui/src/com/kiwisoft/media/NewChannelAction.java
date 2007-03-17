package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 12:44:03
 * To change this template use File | Settings | File Templates.
 */
public class NewChannelAction extends ContextAction<Channel>
{
	public NewChannelAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		ChannelDetailsView.create(null);
	}
}
