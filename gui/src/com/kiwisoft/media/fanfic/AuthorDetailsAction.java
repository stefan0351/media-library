package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class AuthorDetailsAction extends SimpleContextAction
{
	public AuthorDetailsAction()
	{
		super(Author.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		AuthorDetailsView.create((Author)getObject());
	}
}
