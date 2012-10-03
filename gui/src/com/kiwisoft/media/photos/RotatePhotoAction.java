package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.utils.Utils;

public class RotatePhotoAction extends MultiContextAction
{
	private ApplicationFrame frame;

	private int angle;

	public RotatePhotoAction(ApplicationFrame frame, int angle)
	{
		super(Photo.class, "Rotate "+angle+"°", angle==0 ? null : Icons.getIcon("rotate"+angle));
		this.frame=frame;
		this.angle=angle;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final List<Photo> photos=Utils.cast(getObjects());
		for (final Photo photo : photos)
		{
			boolean success=DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					photo.setRotation((photo.getRotation()+angle)%360);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
			if (!success) break;
		}
	}
}
