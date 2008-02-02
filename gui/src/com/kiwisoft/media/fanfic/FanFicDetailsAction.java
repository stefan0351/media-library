package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class FanFicDetailsAction extends SimpleContextAction
{
	public FanFicDetailsAction()
	{
		super(FanFic.class, "Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		FanFicDetailsView.create((FanFic)getObject());
	}
}
