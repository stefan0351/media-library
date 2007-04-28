package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.util.List;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.MultiContextAction;

public class DeletePhotoAction extends MultiContextAction<Photo>
{
	private ApplicationFrame frame;

	public DeletePhotoAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		final List<Photo> photos=getObjects();
		for (Photo photo : photos)
		{
			if (photo.isUsed())
			{
				showMessageDialog(frame, "The photo '"+photo.getId()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
				return;
			}
		}
		int option=showConfirmDialog(frame, "Should photos really be deleted?", "Delete?", YES_NO_OPTION, QUESTION_MESSAGE);
		if (option==YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					for (Photo photo : photos) photo.getBook().dropPhoto(photo);
				}

				public void handleError(Throwable throwable)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", ERROR_MESSAGE);
				}
			});
		}
	}
}
