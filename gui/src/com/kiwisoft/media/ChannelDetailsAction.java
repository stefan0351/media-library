package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

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
	public void actionPerformed(ActionEvent e)
	{
		ChannelDetailsView.create((Channel)getObject());
	}
}
