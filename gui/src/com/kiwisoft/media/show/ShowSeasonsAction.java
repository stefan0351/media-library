package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.SeasonsView;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:53:10
 * To change this template use File | Settings | File Templates.
 */
public class ShowSeasonsAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowSeasonsAction(ApplicationFrame frame)
	{
		super("Staffeln");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new SeasonsView(getObject()), true);
	}
}
