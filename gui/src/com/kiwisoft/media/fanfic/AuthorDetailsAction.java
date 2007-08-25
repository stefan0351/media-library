package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 14:23:02
 * To change this template use File | Settings | File Templates.
 */
public class AuthorDetailsAction extends SimpleContextAction<Author>
{
	public AuthorDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		AuthorDetailsView.create(getObject());
	}
}
