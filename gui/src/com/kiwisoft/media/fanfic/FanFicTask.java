package com.kiwisoft.media.fanfic;

import com.kiwisoft.utils.gui.MenuSidebarItem;

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
		super("Fan Fiction");
		add(new FanFicAuthorsTask());
		add(new FanFicPairingsTask());
		add(new FanDomsTask());
	}
}
