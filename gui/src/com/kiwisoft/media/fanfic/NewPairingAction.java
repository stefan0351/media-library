package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class NewPairingAction extends ContextAction<Pairing>
{
	public NewPairingAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		PairingDetailsView.create(null);
	}
}
