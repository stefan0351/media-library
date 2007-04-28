package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

/**
 * @author Stefan Stiller
 */
public class VideoRecordingsAction extends SimpleContextAction<Video>
{
	private ApplicationFrame frame;

	public VideoRecordingsAction(ApplicationFrame frame)
	{
		super("Records", Icons.getIcon("records"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new RecordingsView(getObject()), true);
	}
}
