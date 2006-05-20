package com.kiwisoft.media.ui.fanfic;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.ui.fanfic.AuthorsView;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:59:21
 * To change this template use File | Settings | File Templates.
 */
public class FanFicAuthorsTask extends MenuSidebarItem.Task
{
	public FanFicAuthorsTask()
	{
		super("Autoren", IconManager.getIcon("com/kiwisoft/media/icons/fanfic16.gif"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new AuthorsView(), true);
	}
}
