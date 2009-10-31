package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class CreateSearchPatternAction extends ContextAction
{
	public CreateSearchPatternAction()
	{
		super("New", Icons.getIcon("add"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		SearchPatternDetailsView.create(null);
	}
}
