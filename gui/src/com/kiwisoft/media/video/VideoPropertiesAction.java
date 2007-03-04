package com.kiwisoft.media.video;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.VideoDetailsView;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 22:28:21
 * To change this template use File | Settings | File Templates.
 */
public class VideoPropertiesAction extends SimpleContextAction<Video>
{
	public VideoPropertiesAction()
	{
		super("Eigenschaften");
	}

	public void actionPerformed(ActionEvent e)
	{
		VideoDetailsView.create(getObject());
	}
}
