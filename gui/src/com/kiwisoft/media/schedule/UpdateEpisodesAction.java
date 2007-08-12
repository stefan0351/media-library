package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.EpisodeUpdater;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

/**
 * @author Stefan Stiller
 */
public class UpdateEpisodesAction extends MultiContextAction<Airdate>
{
	private ApplicationFrame frame;

	public UpdateEpisodesAction(ApplicationFrame frame)
	{
		super("Update References");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(frame, new EpisodeUpdater(getObjects())).show();
	}
}
