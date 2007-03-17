package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 11:36:45
 * To change this template use File | Settings | File Templates.
 */
public class NewRecordAction extends ContextAction<Recording>
{
	private Video video;

	public NewRecordAction(Video video)
	{
		super("New", Icons.getIcon("add"));
		this.video=video;
	}

	public void actionPerformed(ActionEvent e)
	{
		RecordingDetailsView.create(video);
	}
}
