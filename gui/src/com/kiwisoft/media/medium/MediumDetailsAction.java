package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class MediumDetailsAction extends SimpleContextAction<Medium>
{
	public MediumDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		MediumDetailsView.create(getObject());
	}
}
