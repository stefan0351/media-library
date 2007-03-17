package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:51:01
 * To change this template use File | Settings | File Templates.
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
