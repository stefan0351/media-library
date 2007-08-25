package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.ContextAction;

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
