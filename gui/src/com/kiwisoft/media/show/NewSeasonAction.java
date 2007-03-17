package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 19:39:48
 * To change this template use File | Settings | File Templates.
 */
public class NewSeasonAction extends ContextAction<Season>
{
	private Show show;

	public NewSeasonAction(Show show)
	{
		super("New", Icons.getIcon("add"));
		this.show=show;
	}

	public void actionPerformed(ActionEvent e)
	{
		SeasonDetailsView.create(show);
	}
}
