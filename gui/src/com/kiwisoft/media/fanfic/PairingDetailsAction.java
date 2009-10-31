package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class PairingDetailsAction extends SimpleContextAction
{
	public PairingDetailsAction()
	{
		super(Pairing.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PairingDetailsView.create((Pairing)getObject());
	}
}
