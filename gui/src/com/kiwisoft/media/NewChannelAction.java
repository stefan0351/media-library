package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * @author Stefan Stiller
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
