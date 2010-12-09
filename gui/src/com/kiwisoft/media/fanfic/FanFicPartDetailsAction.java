package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;

/**
 * @author Stefan Stiller
 */
public class FanFicPartDetailsAction extends SimpleContextAction
{
	public FanFicPartDetailsAction()
	{
		super(FanFicPart.class, "Details", Icons.getIcon("details"));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		FanFicPart part=(FanFicPart) getObject();
		if ("html".equals(part.getType())) HtmlPartDetailsView.create(part);
		else if ("image".equals(part.getType())) ImagePartDetailsView.create(part);
	}
}