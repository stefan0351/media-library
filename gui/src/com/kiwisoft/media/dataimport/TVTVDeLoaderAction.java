package com.kiwisoft.media.dataimport;

import static javax.swing.JOptionPane.YES_OPTION;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.swing.progress.ProgressDialog;

public class TVTVDeLoaderAction extends AbstractAction
{
	private JFrame frame;

	public TVTVDeLoaderAction(JFrame frame)
	{
		super("Load Schedule from TVTV.de");
		this.frame=frame;
	}

	public void actionPerformed(final ActionEvent anEvent)
	{
		TVTVDeLoader job=new TVTVDeLoader(null)
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
