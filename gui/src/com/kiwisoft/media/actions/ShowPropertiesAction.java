package com.kiwisoft.media.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowDetailsView;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:18
 * To change this template use File | Settings | File Templates.
 */
public class ShowPropertiesAction extends AbstractAction
{
	private Show show;

	public ShowPropertiesAction(Show show)
	{
		super("Eigenschaften");
		this.show=show;
		if (show==null) setEnabled(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		ShowDetailsView.create(show);
	}
}
