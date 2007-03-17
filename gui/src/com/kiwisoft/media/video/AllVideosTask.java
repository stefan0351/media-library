package com.kiwisoft.media.video;

import com.kiwisoft.utils.gui.MenuSidebarItem;

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
		super("Videos, DVD's");
		add(new VideosTask(MediumType.VHS));
		add(new VideosTask(MediumType.VHS_ORIGINAL));
		add(new VideosTask(MediumType.DVD));
		add(new VideosTask(MediumType.DVD_ORIGINAL));
		add(new VideosTask(MediumType.VCD));
	}
}
