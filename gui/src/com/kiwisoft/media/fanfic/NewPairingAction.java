package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

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
