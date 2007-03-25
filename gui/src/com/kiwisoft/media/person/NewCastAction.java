package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * @author Stefan Stiller
 */
public class NewCastAction extends ContextAction<CastMember>
{
	private Show show;
	private CreditType castType;

	public NewCastAction(Show show, CreditType castType)
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
