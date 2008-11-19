package com.kiwisoft.media.files;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class MediaFileDetailsAction extends SimpleContextAction
{
	public MediaFileDetailsAction()
	{
		super(MediaFile.class, "Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		MediaFile mediaFile=(MediaFile)getObject();
		if (mediaFile.getMediaType()==MediaFile.IMAGE) ImageDetailsView.create(mediaFile);
		else if (mediaFile.getMediaType()==MediaFile.VIDEO) VideoDetailsView.create(mediaFile);
	}
}
