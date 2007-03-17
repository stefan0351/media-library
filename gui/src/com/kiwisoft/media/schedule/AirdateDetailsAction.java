package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.media.Airdate;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 19:57:28
 * To change this template use File | Settings | File Templates.
 */
public class AirdateDetailsAction extends SimpleContextAction<Airdate>
{
	protected AirdateDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		AirdateDetailsView.create(getObject());
	}
}
