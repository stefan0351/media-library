package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class PhotosAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public PhotosAction(ApplicationFrame frame)
	{
		super(PhotoGalleryNode.class, "Photos", Icons.getIcon("photos"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PhotoGalleryNode node=(PhotoGalleryNode) getObject();
		frame.setCurrentView(new PhotosView(node.getUserObject()));
	}
}
