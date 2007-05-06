package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

/**
 * @author Stefan Stiller
 */
public class CreateThumbnailsAction extends ContextAction<Picture>
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
		new ProgressDialog(frame, new ThumbnailCreation()).setVisible(true);
	}
}
