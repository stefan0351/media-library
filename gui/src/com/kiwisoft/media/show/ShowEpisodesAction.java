package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.EpisodesView;

import java.awt.event.ActionEvent;

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
		super("Episoden", IconManager.getIcon("com/kiwisoft/media/icons/episode.gif"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new EpisodesView(getObject()), true);
	}
}
