package com.kiwisoft.media.dataimport;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Stefan Stiller
 * @since 27.02.11
 */
public class CountryDataDetailsAction extends SimpleContextAction
{
	private Component component;

	public CountryDataDetailsAction(Component component)
	{
		super(CountryData.class, "Details", Icons.getIcon("details"));
		this.component=component;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		CountryData countryData=(CountryData) getObject();
		CountryDataDetailsView.create(SwingUtilities.getWindowAncestor(component), countryData);
	}
}
