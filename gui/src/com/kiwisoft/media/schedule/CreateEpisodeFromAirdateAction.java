package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class CreateEpisodeFromAirdateAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public CreateEpisodeFromAirdateAction(ApplicationFrame frame)
	{
		super(Airdate.class, "Create Episode");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(frame, (Airdate)getObject());
	}
}
