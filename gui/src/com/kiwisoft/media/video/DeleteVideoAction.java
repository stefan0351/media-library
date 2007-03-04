package com.kiwisoft.media.video;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import com.kiwisoft.media.video.Video;
import com.kiwisoft.media.video.VideoManager;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 22:33:53
 * To change this template use File | Settings | File Templates.
 */
public class DeleteVideoAction extends SimpleContextAction<Video>
{
	private ApplicationFrame frame;

	public DeleteVideoAction(ApplicationFrame frame)
	{
		super("Löschen");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		final Video video=getObject();
		if (video.isUsed())
		{
			showMessageDialog(frame, "Das Video '"+video.getName()+"' kann nicht gelöscht werden.", "Meldung", INFORMATION_MESSAGE);
			return;
		}
		int option=showConfirmDialog(frame, "Das Video '"+video.getName()+"' wirklick löschen?", "Löschen?", YES_NO_OPTION, QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					VideoManager.getInstance().dropVideo(video);
				}

				public void handleError(Throwable throwable)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
