package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class NewPhotoBookAction extends ContextAction<PhotoBook>
{
	public NewPhotoBookAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PhotoBookDetailsView.create();
	}
}
