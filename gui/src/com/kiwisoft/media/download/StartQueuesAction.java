package com.kiwisoft.media.download;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
*/
class StartQueuesAction extends ContextAction
{
	public StartQueuesAction()
	{
		super("Start", Icons.getIcon("play"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GrabberUtils.startAllQueues();
	}
}
