package com.kiwisoft.media.medium;

import com.kiwisoft.app.MenuSidebarItem;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class MediaTask extends MenuSidebarItem.Task
{
	private MediumType type;

	public MediaTask(MediumType type)
	{
		super(type.getPluralName());
		this.type=type;
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new MediaView(type), true);
	}
}
