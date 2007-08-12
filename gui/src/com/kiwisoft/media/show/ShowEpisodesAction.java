package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowEpisodesAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowEpisodesAction(ApplicationFrame frame)
	{
		super("Episodes");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new EpisodesView(getObject()), true);
	}
}
