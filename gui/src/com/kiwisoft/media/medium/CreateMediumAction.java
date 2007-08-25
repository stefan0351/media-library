package com.kiwisoft.media.medium;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;

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
