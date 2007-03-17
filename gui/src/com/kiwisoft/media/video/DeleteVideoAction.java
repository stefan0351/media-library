package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

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
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		final Video video=getObject();
		if (video.isUsed())
		{
			showMessageDialog(frame, "The video '"+video.getName()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
			return;
		}
		int option=showConfirmDialog(frame, "Delete video '"+video.getName()+"'?", "Confirmation", YES_NO_OPTION, QUESTION_MESSAGE);
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
