package com.kiwisoft.media.medium;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.utils.Utils;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class MediaBulkChangeAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public MediaBulkChangeAction(ApplicationFrame frame)
	{
		super(Medium.class, "Bulk Change");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		MediaBulkChangeView.create(frame, Utils.<Medium>cast(getObjects()));
	}
}
