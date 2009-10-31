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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		MediaFile mediaFile=(MediaFile)getObject();
		if (mediaFile.getMediaType()==MediaType.IMAGE) ImageDetailsView.create(mediaFile);
		else if (mediaFile.getMediaType()==MediaType.VIDEO) VideoDetailsView.create(mediaFile);
		else if (mediaFile.getMediaType()==MediaType.AUDIO) AudioDetailsView.create(mediaFile);
	}
}
