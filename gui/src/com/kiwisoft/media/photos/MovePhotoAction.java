package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.util.List;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.utils.Utils;

public class MovePhotoAction extends MultiContextAction
{
	private ApplicationFrame frame;
	private PhotoGallery gallery;

	public MovePhotoAction(ApplicationFrame frame, PhotoGallery gallery)
	{
		super(Photo.class, "Move to Another Gallery");
		this.frame=frame;
		this.gallery=gallery;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final List<Photo> photos=Utils.cast(getObjects());
		final MovePhotoDialog dialog=new MovePhotoDialog(frame, gallery);
		dialog.setVisible(true);
		if (dialog.isOk())
		{
			DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					PhotoGallery newGallery;
					if (dialog.isNewGallery())
					{
						newGallery=PhotoManager.getInstance().createRootGallery();
						newGallery.setName(dialog.getGalleryName());
					}
					else newGallery=dialog.getGallery();
					for (Photo photo : photos)
					{
						photo.getGallery().removePhoto(photo);
						newGallery.addPhoto(photo);
					}
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(frame, throwable);
				}
			});
		}
	}
}
