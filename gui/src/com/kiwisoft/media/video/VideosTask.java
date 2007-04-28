package com.kiwisoft.media.video;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.MenuSidebarItem;

/**
 * @author Stefan Stiller
 */
public class VideosTask extends MenuSidebarItem.Task
{
	private MediumType type;

	public VideosTask(MediumType type)
	{
		super(type.getPluralName());
		this.type=type;
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new VideosView(type), true);
	}
}
