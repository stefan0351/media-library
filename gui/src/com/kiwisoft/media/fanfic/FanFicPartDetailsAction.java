package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class FanFicPartDetailsAction extends SimpleContextAction
{
	public FanFicPartDetailsAction()
	{
		super(FanFicPart.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FanFicPartDetailsView.create((FanFicPart)getObject());
	}
}