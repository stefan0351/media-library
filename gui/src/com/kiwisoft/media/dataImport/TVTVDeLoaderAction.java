/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:39 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;

import com.kiwisoft.media.dataImport.TVTVDeLoaderDialog;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;

public class TVTVDeLoaderAction<T> extends MultiContextAction<T>
{
	private ApplicationFrame frame;

	public TVTVDeLoaderAction(ApplicationFrame frame)
	{
		super("Lade Termine von TVTV.de");
		this.frame=frame;
	}

	public void actionPerformed(final ActionEvent anEvent)
	{
		new TVTVDeLoaderDialog<T>(frame, getObjects()).setVisible(true);
	}
}
