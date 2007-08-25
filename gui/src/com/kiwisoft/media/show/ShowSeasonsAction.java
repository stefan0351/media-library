package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowSeasonsAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowSeasonsAction(ApplicationFrame frame)
	{
		super("Seasons");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new SeasonsView(getObject()), true);
	}
}
