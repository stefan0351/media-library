package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class CreateAirdateAction extends ContextAction
{
	public CreateAirdateAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		AirdateDetailsView.create((Show)null);
	}
}
