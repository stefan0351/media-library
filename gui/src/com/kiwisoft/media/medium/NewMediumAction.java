package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class NewMediumAction extends ContextAction<Medium>
{
	public NewMediumAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		MediumDetailsView.create();
	}
}
