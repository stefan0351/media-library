package com.kiwisoft.media;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 11:10:39
 * To change this template use File | Settings | File Templates.
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
