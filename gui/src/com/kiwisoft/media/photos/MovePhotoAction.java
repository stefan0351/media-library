package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.utils.gui.GuiUtils;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.app.ApplicationFrame;

public class MovePhotoAction extends MultiContextAction<Photo>
{
	private ApplicationFrame frame;
	private PhotoGallery gallery;

	public MovePhotoAction(ApplicationFrame frame, PhotoGallery gallery)
	{
		super("Move to Another Gallery");
		this.frame=frame;
		this.gallery=gallery;
	}

	public void actionPerformed(ActionEvent e)
	{
		final List<Photo> photos=getObjects();
		final MovePhotoDialog dialog=new MovePhotoDialog(frame, gallery);
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					PhotoGallery newGallery;
					if (dialog.isNewGallery())
					{
						newGallery=PhotoManager.getInstance().createGallery();
						newGallery.setName(dialog.getGalleryName());
					}
					else newGallery=dialog.getGallery();
					for (Photo photo : photos)
					{
						photo.getGallery().removePhoto(photo);
						newGallery.addPhoto(photo);
					}
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(frame, throwable);
				}
			});
		}
	}
}
