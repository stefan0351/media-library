package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class NewCastAction extends ContextAction
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
