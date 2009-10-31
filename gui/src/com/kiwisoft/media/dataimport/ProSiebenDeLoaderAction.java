/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:53 PM
 */
package com.kiwisoft.media.dataimport;

import java.awt.event.ActionEvent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.swing.actions.MultiContextAction;

public class ProSiebenDeLoaderAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public ProSiebenDeLoaderAction(ApplicationFrame frame)
	{
		super(Show.class, "Load Schedule from ProSieben.de");
		this.frame=frame;
	}

	@Override
	public void actionPerformed(final ActionEvent anEvent)
	{
		new ProSiebenDeLoaderDialog(frame, getObjects()).setVisible(true);
	}
}
