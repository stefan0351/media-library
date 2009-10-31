package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.EpisodeUpdater;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.utils.Utils;

/**
 * @author Stefan Stiller
 */
public class UpdateEpisodesAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public UpdateEpisodesAction(ApplicationFrame frame)
	{
		super(Airdate.class, "Update References");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		new ProgressDialog(frame, new EpisodeUpdater(Utils.<Airdate>cast(getObjects()))).start();
	}
}
