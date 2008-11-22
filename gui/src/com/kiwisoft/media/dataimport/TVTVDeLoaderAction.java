package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

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
		new ProgressDialog(frame, new TVTVDeLoader(null)).start();
	}
}
