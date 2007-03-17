package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CastDetailsView;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 21:26:53
 * To change this template use File | Settings | File Templates.
 */
public class NewCastAction extends ContextAction<CastMember>
{
	private Show show;
	private int castType;

	public NewCastAction(Show show, int castType)
	{
		super("New", Icons.getIcon("add"));
		this.show=show;
		this.castType=castType;
	}

	public void actionPerformed(ActionEvent e)
	{
		CastDetailsView.create(show, castType);
	}
}
