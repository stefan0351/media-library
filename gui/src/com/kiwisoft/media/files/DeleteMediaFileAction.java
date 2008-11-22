package com.kiwisoft.media.files;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;

public class DeleteMediaFileAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public DeleteMediaFileAction(ApplicationFrame frame)
	{
		super(MediaFile.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		final List<MediaFile> mediafiles=getObjects();
		for (MediaFile mediafile : mediafiles)
		{
			if (mediafile.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "The media file '"+mediafile.getId()+"' can't be deleted because it is used.", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=
			JOptionPane.showConfirmDialog(frame, "Delete media files physically?", "Delete?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION) DBSession.execute(new MyTransactional(mediafiles, true));
		else if (option==JOptionPane.NO_OPTION) DBSession.execute(new MyTransactional(mediafiles, false));
	}

	private class MyTransactional implements Transactional
	{
		private final List<MediaFile> mediafiles;
		private boolean deletePhysically;

		public MyTransactional(List<MediaFile> mediafiles, boolean deletePhysically)
		{
			this.mediafiles=mediafiles;
			this.deletePhysically=deletePhysically;
		}

		public void run() throws Exception
		{
			for (MediaFile mediafile : mediafiles) MediaFileManager.getInstance().dropMediaFile(mediafile, deletePhysically);
		}

		public void handleError(Throwable throwable, boolean rollback)
		{
			JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
