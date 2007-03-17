package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 19:38:16
 * To change this template use File | Settings | File Templates.
 */
public class SeasonDetailsAction extends SimpleContextAction<Season>
{
	protected SeasonDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		SeasonDetailsView.create(getObject());
	}
}
