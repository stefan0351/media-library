package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class PhotosAction extends SimpleContextAction<PhotoBook>
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
