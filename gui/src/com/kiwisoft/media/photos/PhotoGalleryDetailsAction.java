package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PhotoGalleryDetailsAction extends SimpleContextAction<PhotoGallery>
{
	public PhotoGalleryDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PhotoGalleryDetailsView.create(getObject());
	}
}
