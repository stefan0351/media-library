package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class NewAuthorAction extends ContextAction<Author>
{
	public NewAuthorAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		AuthorDetailsView.create(null);
	}
}
