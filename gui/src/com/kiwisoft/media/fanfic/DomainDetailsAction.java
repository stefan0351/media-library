package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class DomainDetailsAction extends SimpleContextAction
{
	public DomainDetailsAction()
	{
		super(FanDom.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FanDomDetailsView.create((FanDom)getObject());
	}
}
