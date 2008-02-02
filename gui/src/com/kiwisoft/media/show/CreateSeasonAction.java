package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.swing.actions.MultiContextAction;

/**
 * @author Stefan Stiller
 */
public class CreateSeasonAction extends MultiContextAction
{
	public CreateSeasonAction()
	{
		super(Episode.class, "Create Season");
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Episode> episodes=getObjects();
		SeasonDetailsView.create(episodes.get(0), episodes.get(episodes.size()-1));
	}
}
