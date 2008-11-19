package com.kiwisoft.media.files;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class CheckPicturesAction extends ContextAction
{
	private ApplicationFrame frame;

	public CheckPicturesAction(ApplicationFrame frame)
	{
		super("Check References");
		putValue(SHORT_DESCRIPTION, "Check if the referenced files exist.");
		this.frame=frame;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(frame, new MediaFileChecker()).start();
	}
}
