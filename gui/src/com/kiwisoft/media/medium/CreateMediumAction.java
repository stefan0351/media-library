package com.kiwisoft.media.medium;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;

import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 */
public class CreateMediumAction extends MultiContextAction
{
	public CreateMediumAction()
	{
		super(Recordable.class, "Create Medium", Icons.getIcon("medium.add"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		MediumDetailsView.create(getObjects());
	}
}
