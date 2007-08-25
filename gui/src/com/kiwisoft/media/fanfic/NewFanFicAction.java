package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 15:28:23
 * To change this template use File | Settings | File Templates.
 */
public class NewFanFicAction extends ContextAction<FanFic>
{
	private FanFicGroup group;

	public NewFanFicAction(FanFicGroup group)
	{
		super("New", Icons.getIcon("add"));
		this.group=group;
	}

	public void actionPerformed(ActionEvent e)
	{
		FanFicDetailsView.create(group);
	}
}
