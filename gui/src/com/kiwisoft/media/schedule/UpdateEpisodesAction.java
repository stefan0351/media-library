package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.show.EpisodeUpdater;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.progress.ProgressDialog;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 20:12:08
 * To change this template use File | Settings | File Templates.
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
