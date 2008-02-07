package com.kiwisoft.media.download;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
*/
class StopDownloadAction extends ContextAction
{
	private DownloadProject project;

	public StopDownloadAction(DownloadProject project
	)
	{
		super("Stop", Icons.getIcon("stop"));
		this.project=project;
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		project.stop();
	}
}
