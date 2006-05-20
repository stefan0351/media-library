package com.kiwisoft.media.ui.fanfic;

import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.MenuSidebarItem;
import com.kiwisoft.media.ui.fanfic.FanDomsTask;
import com.kiwisoft.media.ui.fanfic.FanFicAuthorsTask;
import com.kiwisoft.media.ui.fanfic.FanFicPairingsTask;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 13.11.2004
 * Time: 21:57:26
 * To change this template use File | Settings | File Templates.
 */
public class FanFicTask extends MenuSidebarItem.Task
{
	public FanFicTask()
	{
		super("Fan Fiction", IconManager.getIcon("com/kiwisoft/media/icons/fanfic32.gif"));
		add(new FanFicAuthorsTask());
		add(new FanFicPairingsTask());
		add(new FanDomsTask());
	}
}
