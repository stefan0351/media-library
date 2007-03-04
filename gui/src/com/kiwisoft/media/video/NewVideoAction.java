package com.kiwisoft.media.video;

import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.video.VideoDetailsView;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.utils.gui.actions.ContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 22:28:53
 * To change this template use File | Settings | File Templates.
 */
public class NewVideoAction extends ContextAction<Video>
{
	private MediumType type;

	public NewVideoAction(MediumType type)
	{
		super("Neu");
		this.type=type;
	}

	public void actionPerformed(ActionEvent e)
	{
		VideoDetailsView.create(type);
	}
}
