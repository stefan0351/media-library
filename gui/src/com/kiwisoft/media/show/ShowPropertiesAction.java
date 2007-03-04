package com.kiwisoft.media.show;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowDetailsView;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 01.03.2007
 * Time: 12:17:18
 * To change this template use File | Settings | File Templates.
 */
public class ShowPropertiesAction extends SimpleContextAction<Show>
{
	public ShowPropertiesAction()
	{
		super("Eigenschaften");
	}

	public void actionPerformed(ActionEvent e)
	{
		ShowDetailsView.create(getObject());
	}
}
