package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class ShowCastAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowCastAction(ApplicationFrame frame)
	{
		super("Cast", Icons.getIcon("cast"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ShowCastView(getObject()), true);
	}
}
