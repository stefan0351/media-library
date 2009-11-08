package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;

public class DeletePhotoGalleryAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public DeletePhotoGalleryAction(ApplicationFrame frame)
	{
		super(PhotoGalleryNode.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		PhotoGalleryNode node=(PhotoGalleryNode)getObject();
		final PhotoGallery gallery=node.getUserObject();
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
				@Override
				public void run() throws Exception
				{
					PhotoGallery parent=gallery.getParent();
					if (parent!=null) parent.dropChildGallery(gallery);
					else PhotoManager.getInstance().dropRootGallery(gallery);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
