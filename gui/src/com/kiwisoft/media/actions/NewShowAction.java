package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.kiwisoft.media.show.ShowDetailsView;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:59
 * To change this template use File | Settings | File Templates.
 */
public class NewShowAction extends AbstractAction
{
	public NewShowAction()
	{
		super("Neu");
	}

	public void actionPerformed(ActionEvent e)
	{
		ShowDetailsView.create(null);
	}
}
