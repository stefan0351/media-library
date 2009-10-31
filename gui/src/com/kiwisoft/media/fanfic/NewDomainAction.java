package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewDomainAction extends ContextAction
{
	public NewDomainAction()
	{
		super("New", Icons.getIcon("add"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FanDomDetailsView.create(null);
	}
}
