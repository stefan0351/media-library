package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class PhotosAction extends SimpleContextAction<PhotoGallery>
{
	private ApplicationFrame frame;

	public PhotosAction(ApplicationFrame frame)
	{
		super("Photos", Icons.getIcon("photos"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new PhotosView(getObject()), true);
	}
}
