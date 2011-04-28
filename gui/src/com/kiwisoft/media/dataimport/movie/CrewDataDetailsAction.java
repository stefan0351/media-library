package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.media.dataimport.CrewData;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class CrewDataDetailsAction extends SimpleContextAction
{
	private Component component;

	public CrewDataDetailsAction(Component component)
	{
		super(CrewData.class, "Details", Icons.getIcon("details"));
		this.component=component;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		CrewData crewData=(CrewData) getObject();
		CrewDataDetailsView.create(SwingUtilities.getWindowAncestor(component), crewData);
	}
}
