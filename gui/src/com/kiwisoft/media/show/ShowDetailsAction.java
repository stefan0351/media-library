package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowDetailsAction extends SimpleContextAction<Show>
{
	public ShowDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		ShowDetailsView.create(getObject());
	}
}
