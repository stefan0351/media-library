package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class BookDetailsAction extends SimpleContextAction
{
	public BookDetailsAction()
	{
		super(Book.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		BookDetailsView.create((Book)getObject());
	}
}
