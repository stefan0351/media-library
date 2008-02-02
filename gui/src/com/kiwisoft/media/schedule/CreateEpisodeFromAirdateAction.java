package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class CreateEpisodeFromAirdateAction extends SimpleContextAction
{
	public CreateEpisodeFromAirdateAction()
	{
		super(Airdate.class, "Create Episode");
	}

	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create((Airdate)getObject());
	}
}
