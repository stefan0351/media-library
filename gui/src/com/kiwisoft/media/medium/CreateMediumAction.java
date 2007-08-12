package com.kiwisoft.media.medium;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.Icons;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class CreateMediumAction<T extends Recordable> extends MultiContextAction<T>
{
	public CreateMediumAction()
	{
		super("Create Medium", Icons.getIcon("medium.add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		MediumDetailsView.create(getObjects());
	}
}
