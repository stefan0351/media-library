package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewPictureAction extends ContextAction
{
	public NewPictureAction()
	{
		super("Add", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PictureDetailsView.create(null);
	}
}
