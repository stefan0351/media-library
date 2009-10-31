package com.kiwisoft.media.download;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
*/
class StopQueuesAction extends ContextAction
{
	public StopQueuesAction()
	{
		super("Stop", Icons.getIcon("stop"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GrabberUtils.stopAllQueues();
	}
}
