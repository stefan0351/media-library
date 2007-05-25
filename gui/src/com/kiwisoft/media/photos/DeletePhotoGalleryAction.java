package com.kiwisoft.media.photos;

import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

public class DeletePhotoGalleryAction extends SimpleContextAction<PhotoGallery>
{
	private ApplicationFrame frame;

	public DeletePhotoGalleryAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		final PhotoGallery gallery=getObject();
		if (gallery.isUsed())
		{
			showMessageDialog(frame, "The photo gallery '"+gallery.getName()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
			return;
		}
		int option=showConfirmDialog(frame, "Should photo gallery really be deleted?", "Delete?", YES_NO_OPTION, QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					PhotoManager.getInstance().dropGallery(gallery);
				}

				public void handleError(Throwable throwable)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}