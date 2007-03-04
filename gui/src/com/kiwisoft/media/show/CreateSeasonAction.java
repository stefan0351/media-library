package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.SeasonDetailsView;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 18:36:02
 * To change this template use File | Settings | File Templates.
 */
public class CreateSeasonAction extends MultiContextAction<Episode>
{
	public CreateSeasonAction()
	{
		super("Erzeuge Staffel");
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Episode> episodes=getObjects();
		SeasonDetailsView.create(episodes.get(0), episodes.get(episodes.size()-1));
	}
}
