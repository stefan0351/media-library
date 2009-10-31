package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.show.Production;
import com.kiwisoft.swing.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class NewCastAction extends ContextAction
{
	private CreditType castType;
	private Production production;

	public NewCastAction(Production production, CreditType castType)
	{
		super("New "+castType.getAsName());
		this.production=production;
		this.castType=castType;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		CastDetailsView.create(production, castType);
	}
}
