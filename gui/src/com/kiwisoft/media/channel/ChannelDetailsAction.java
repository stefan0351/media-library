package com.kiwisoft.media.channel;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.media.Channel;

/**
 * @author Stefan Stiller
 */
public class ChannelDetailsAction extends SimpleContextAction
{
	protected ChannelDetailsAction()
	{
		super(Channel.class, "Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		ChannelDetailsView.create((Channel)getObject());
	}
}
