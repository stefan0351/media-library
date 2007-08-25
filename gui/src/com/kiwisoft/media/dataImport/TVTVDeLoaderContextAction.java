/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:39 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.app.ApplicationFrame;

public class TVTVDeLoaderContextAction<T> extends MultiContextAction<T>
{
	private ApplicationFrame frame;

	public TVTVDeLoaderContextAction(ApplicationFrame frame)
	{
		super("Load Schedule from TVTV.de");
		this.frame=frame;
	}

	public void actionPerformed(final ActionEvent anEvent)
	{
		new TVTVDeLoaderDialog<T>(frame, getObjects()).setVisible(true);
	}
}
