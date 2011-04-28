package com.kiwisoft.media.dataimport.movie;

import com.kiwisoft.media.dataimport.CastData;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 * @since 26.02.11
 */
public class CastDataDetailsAction extends SimpleContextAction
{
	private Component component;

	public CastDataDetailsAction(Component component)
	{
		super(CastData.class, "Details", Icons.getIcon("details"));
		this.component=component;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		CastData castData=(CastData) getObject();
		CastDataDetailsView.create(SwingUtilities.getWindowAncestor(component), castData);
	}
}
