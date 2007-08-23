package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class ChannelDetailsAction extends SimpleContextAction<Channel>
{
	protected ChannelDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		ChannelDetailsView.create(getObject());
	}
}
