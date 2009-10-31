package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
*/
public class SeasonEpisodesAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public SeasonEpisodesAction(ApplicationFrame frame)
	{
		super(Season.class, "Episodes");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new EpisodesView((Season)getObject()));
	}
}
