package com.kiwisoft.media.medium;

import com.kiwisoft.app.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class AllMediaTask extends MenuSidebarItem.Task
{
	public AllMediaTask()
	{
		super("Media");
		add(new MediaTask(MediumType.VHS));
		add(new MediaTask(MediumType.VHS_ORIGINAL));
		add(new MediaTask(MediumType.DVD));
		add(new MediaTask(MediumType.DVD_ORIGINAL));
		add(new MediaTask(MediumType.DVD_RW));
		add(new MediaTask(MediumType.VCD));
	}
}
