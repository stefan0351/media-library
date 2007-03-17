package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.ContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 14:24:07
 * To change this template use File | Settings | File Templates.
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
