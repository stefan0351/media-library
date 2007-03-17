package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:59
 * To change this template use File | Settings | File Templates.
 */
public class NewShowAction extends ContextAction<Show>
{
	public NewShowAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void update(List<Show> objects)
	{
		setEnabled(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		ShowDetailsView.create(null);
	}
}
