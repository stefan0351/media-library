package com.kiwisoft.media.fanfic;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.fanfic.PairingsView;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:59:51
 * To change this template use File | Settings | File Templates.
 */
public class FanFicPairingsTask extends MenuSidebarItem.Task
{
	public FanFicPairingsTask()
	{
		super("Paare", IconManager.getIcon("com/kiwisoft/media/icons/fanfic16.gif"));
	}

	public void perform(ApplicationFrame frame)
	{
		frame.setCurrentView(new PairingsView(), true);
	}
}
