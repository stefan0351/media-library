package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.EpisodeDetailsView;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:41:01
 * To change this template use File | Settings | File Templates.
 */
public class NewEpisodeAction extends ContextAction<Episode>
{
	private Show show;

	public NewEpisodeAction(Show show)
	{
		super("Neu");
		this.show=show;
	}

	public void actionPerformed(ActionEvent e)
	{
		EpisodeDetailsView.create(show);
	}
}
