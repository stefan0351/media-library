package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewBookAction extends ContextAction<Object>
{
	public NewBookAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		BookDetailsView.create(null);
	}
}
