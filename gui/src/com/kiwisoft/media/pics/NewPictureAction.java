package com.kiwisoft.media.pics;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class NewPictureAction extends ContextAction<Picture>
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
