package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class NewMediumAction extends ContextAction<Medium>
{
	private MediumType type;

	public NewMediumAction(MediumType type)
	{
		super("New", Icons.getIcon("add"));
		this.type=type;
	}

	public void actionPerformed(ActionEvent e)
	{
		MediumDetailsView.create(type);
	}
}
