package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class PhotoBookDetailsAction extends SimpleContextAction<PhotoBook>
{
	public PhotoBookDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PhotoBookDetailsView.create(getObject());
	}
}
