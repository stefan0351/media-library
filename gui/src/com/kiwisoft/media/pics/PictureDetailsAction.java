package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PictureDetailsAction extends SimpleContextAction
{
	public PictureDetailsAction()
	{
		super(Picture.class, "Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PictureDetailsView.create((Picture)getObject());
	}
}
