package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class BookDetailsAction extends SimpleContextAction<Book>
{
	public BookDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		BookDetailsView.create(getObject());
	}
}
