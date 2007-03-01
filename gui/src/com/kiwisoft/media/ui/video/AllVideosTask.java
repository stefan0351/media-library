package com.kiwisoft.media.ui.video;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.media.video.MediumType;
import com.kiwisoft.media.ui.video.VideosTask;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:55:30
 * To change this template use File | Settings | File Templates.
 */
public class AllVideosTask extends MenuSidebarItem.Task
{
	public AllVideosTask()
	{
		super("Videos, DVD's", IconManager.getIcon("com/kiwisoft/media/icons/video32.gif"));
		add(new VideosTask(MediumType.VHS));
		add(new VideosTask(MediumType.VHS_ORIGINAL));
		add(new VideosTask(MediumType.DVD));
		add(new VideosTask(MediumType.DVD_ORIGINAL));
		add(new VideosTask(MediumType.VCD));
	}
}
