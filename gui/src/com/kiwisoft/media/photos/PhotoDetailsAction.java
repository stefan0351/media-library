package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PhotoDetailsAction extends SimpleContextAction
{
	public PhotoDetailsAction()
	{
		super(Photo.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PhotoDetailsView.create((Photo)getObject());
	}
}
