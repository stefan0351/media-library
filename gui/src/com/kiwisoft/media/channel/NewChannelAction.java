package com.kiwisoft.media.channel;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.media.channel.ChannelDetailsView;

/**
 * @author Stefan Stiller
 */
public class NewChannelAction extends ContextAction
{
	public NewChannelAction()
	{
		super("New", Icons.getIcon("add"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ChannelDetailsView.create(null);
	}
}
