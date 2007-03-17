package com.kiwisoft.media.dataImport;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

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
		new TVTVDeLoaderDialog(frame, null).setVisible(true);
	}
}
