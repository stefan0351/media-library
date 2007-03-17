package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 15:06:01
 * To change this template use File | Settings | File Templates.
 */
public class FanFicDetailsAction extends SimpleContextAction<FanFic>
{
	public FanFicDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		FanFicDetailsView.create(getObject());
	}
}
