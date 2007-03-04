package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowRecordingsView;
import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.RecordingsView;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:49:16
 * To change this template use File | Settings | File Templates.
 */
public class ShowRecordingsAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public ShowRecordingsAction(ApplicationFrame frame)
	{
		super("Aufnahmen");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		frame.setCurrentView(new ShowRecordingsView(getObject()), true);
	}
}
