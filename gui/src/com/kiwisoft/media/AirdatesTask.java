package com.kiwisoft.media;

import java.util.Calendar;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:55:45
 * To change this template use File | Settings | File Templates.
 */
public class AirdatesTask extends MenuSidebarItem.Task
{
	public AirdatesTask()
	{
		super("Sendetermine", IconManager.getIcon("com/kiwisoft/media/icons/airdates32.gif"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new AirdatesView(Calendar.DATE, 1), true);
	}
}
