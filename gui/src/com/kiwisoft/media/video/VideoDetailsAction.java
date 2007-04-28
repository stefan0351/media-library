package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class VideoDetailsAction extends SimpleContextAction<Video>
{
	public VideoDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		VideoDetailsView.create(getObject());
	}
}
