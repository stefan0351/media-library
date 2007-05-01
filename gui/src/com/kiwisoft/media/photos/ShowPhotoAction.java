package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

public class ShowPhotoAction extends SimpleContextAction<Photo>
{
	public ShowPhotoAction()
	{
		super("Show", Icons.getIcon("photo"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PhotoViewer.create(getObject());
	}
}
