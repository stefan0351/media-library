package com.kiwisoft.media.video;

import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * @author Stefan Stiller
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
		add(new VideosTask(MediumType.DVD_RW));
		add(new VideosTask(MediumType.VCD));
	}
}
