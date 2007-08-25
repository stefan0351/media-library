package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewPhotoGalleryAction extends ContextAction<PhotoGallery>
{
	public NewPhotoGalleryAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PhotoGalleryDetailsView.create();
	}
}
