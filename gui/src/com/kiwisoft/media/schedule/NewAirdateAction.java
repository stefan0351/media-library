package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 20:05:17
 * To change this template use File | Settings | File Templates.
 */
public class NewAirdateAction extends ContextAction<Airdate>
{
	public NewAirdateAction()
	{
		super("New", Icons.getIcon("add"));
	}

	public void actionPerformed(ActionEvent e)
	{
		AirdateDetailsView.create((Show)null);
	}
}
