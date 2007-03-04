package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.LinksView;
import com.kiwisoft.media.show.Show;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:50:19
 * To change this template use File | Settings | File Templates.
 */
public class ShowLinksAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowLinksAction(ApplicationFrame frame)
	{
		super("Links");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new LinksView(getObject()), true);
	}
}
