/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:39 PM
 */
package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.progress.ProgressDialog;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.person.Person;

public class TVTVDeLoaderContextAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public TVTVDeLoaderContextAction(ApplicationFrame frame)
	{
		super(Object.class, "Load Schedule from TVTV.de");
		this.frame=frame;
	}

	@Override
	protected boolean isValid(Object object)
	{
		return object instanceof Show || object instanceof Person;
	}

	public void actionPerformed(final ActionEvent anEvent)
	{
		new ProgressDialog(frame, new TVTVDeLoader(getObjects())).start();
	}
}
