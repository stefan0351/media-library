package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewFanFicAction extends ContextAction
{
	private FanFicGroup group;

	public NewFanFicAction(FanFicGroup group)
	{
		super("New", Icons.getIcon("add"));
		this.group=group;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FanFicDetailsView.create(group);
	}
}
