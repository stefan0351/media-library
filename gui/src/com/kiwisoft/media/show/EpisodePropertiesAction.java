package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 04.03.2007
 * Time: 17:25:33
 * To change this template use File | Settings | File Templates.
 */
public class EpisodePropertiesAction extends SimpleContextAction<Episode>
{
	public EpisodePropertiesAction()
	{
		super("Eigenschaften");
	}

	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(getObject());
	}
}
