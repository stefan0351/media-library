package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PhotoGalleryDetailsAction extends SimpleContextAction
{
	public PhotoGalleryDetailsAction()
	{
		super(PhotoGalleryNode.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PhotoGalleryNode node=(PhotoGalleryNode) getObject();
		PhotoGalleryDetailsView.create(node.getUserObject());
	}
}
