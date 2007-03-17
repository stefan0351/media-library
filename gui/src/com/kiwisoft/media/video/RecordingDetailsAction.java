package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 22:28:21
 * To change this template use File | Settings | File Templates.
 */
public class RecordingDetailsAction extends SimpleContextAction<Recording>
{
	public RecordingDetailsAction()
	{
		super("Details", Icons.getIcon("details"));
	}

	public void actionPerformed(ActionEvent e)
	{
		RecordingDetailsView.create(getObject());
	}
}
