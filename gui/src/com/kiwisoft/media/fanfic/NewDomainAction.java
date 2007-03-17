package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 15:06:19
 * To change this template use File | Settings | File Templates.
 */
public class NewDomainAction extends ContextAction<FanDom>
{
	public NewDomainAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		FanDomDetailsView.create(null);
	}
}
