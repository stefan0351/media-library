package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PhotoDetailsAction extends SimpleContextAction<Photo>
{
	public PhotoDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PhotoDetailsView.create(getObject());
	}
}
