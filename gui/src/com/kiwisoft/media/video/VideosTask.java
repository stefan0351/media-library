package com.kiwisoft.media.video;

import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.video.VideosView;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:55:01
 * To change this template use File | Settings | File Templates.
 */
public class VideosTask extends MenuSidebarItem.Task
{
	private MediumType type;

	public VideosTask(MediumType type)
	{
		super(type.getPluralName(), IconManager.getIcon("com/kiwisoft/media/icons/video16.gif"));
		this.type=type;
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new VideosView(type), true);
	}
}
