package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class SeasonDetailsAction extends SimpleContextAction
{
	protected SeasonDetailsAction()
	{
		super(Season.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		SeasonDetailsView.create((Season)getObject());
	}
}
