package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 20:10:11
 * To change this template use File | Settings | File Templates.
 */
public class CreateEpisodeFromAirdateAction extends SimpleContextAction<Airdate>
{
	public CreateEpisodeFromAirdateAction()
	{
		super("Create Episode");
	}

	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(getObject());
	}
}
