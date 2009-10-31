package com.kiwisoft.media.files;

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
		putValue(SHORT_DESCRIPTION, "Create sidebar thumbnails for all pictures.");
		this.frame=frame;
	}

	/**
	 * Invoked when an action occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(frame, new ThumbnailCreation()).start();
	}
}
