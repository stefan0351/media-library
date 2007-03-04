package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowCastView;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:53:24
 * To change this template use File | Settings | File Templates.
 */
public class ShowCastAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowCastAction(ApplicationFrame frame)
	{
		super("Darsteller");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ShowCastView(getObject()), true);
	}
}
