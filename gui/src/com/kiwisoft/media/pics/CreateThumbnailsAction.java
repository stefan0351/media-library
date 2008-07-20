package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class CreateThumbnailsAction extends ContextAction
{
	private ApplicationFrame frame;

	public CreateThumbnailsAction(ApplicationFrame frame)
	{
		super("Create Thumbnails");
		this.frame=frame;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(frame, new ThumbnailCreation()).start();
	}
}
