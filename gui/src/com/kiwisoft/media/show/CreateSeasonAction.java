package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.SeasonDetailsView;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Stefan Stiller
 */
public class CreateSeasonAction extends MultiContextAction<Episode>
{
	public CreateSeasonAction()
	{
		super("Create Season");
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Episode> episodes=getObjects();
		SeasonDetailsView.create(episodes.get(0), episodes.get(episodes.size()-1));
	}
}
