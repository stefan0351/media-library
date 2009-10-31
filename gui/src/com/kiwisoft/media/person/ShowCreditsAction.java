package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.media.show.Production;

/**
 * @author Stefan Stiller
 */
public class ShowCreditsAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public ShowCreditsAction(ApplicationFrame frame)
	{
		super(Production.class, "Cast and Credits", Icons.getIcon("cast"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ProductionCreditsView((Production)getObject()));
	}
}
