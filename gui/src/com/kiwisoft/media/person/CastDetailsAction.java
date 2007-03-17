package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 21:20:12
 * To change this template use File | Settings | File Templates.
 */
public class CastDetailsAction extends SimpleContextAction<CastMember>
{
	public CastDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		CastDetailsView.create(getObject());
	}
}
