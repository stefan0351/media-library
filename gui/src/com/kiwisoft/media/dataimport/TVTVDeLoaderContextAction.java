/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Oct 26, 2003
 * Time: 12:16:39 PM
 */
package com.kiwisoft.media.dataimport;

import java.awt.event.ActionEvent;
import java.awt.Window;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;

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
		TVTVDeLoader job=new TVTVDeLoader(getObjects())
		{
			@Override
			protected boolean askMissingChannel(String channelName, String channelKey)
			{
				Window window=getProgressSupport().getWindow();
				int option=showConfirmDialog(window, "Create channel '"+channelName+"' ("+channelKey+").", "Create Channel?",
											 YES_NO_OPTION, QUESTION_MESSAGE);
				return option==YES_OPTION;
			}

		};
		new ProgressDialog(frame, job).start();
	}
}
