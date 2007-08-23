package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

public class RotatePhotoAction extends SimpleContextAction<Photo>
{
	private ApplicationFrame frame;

	private int angle;

	public RotatePhotoAction(ApplicationFrame frame, int angle)
	{
		super("Rotate "+angle+"°", angle==0 ? null : Icons.getIcon("rotate"+angle));
		this.frame=frame;
		this.angle=angle;
	}

	public void actionPerformed(ActionEvent e)
	{
		final Photo photo=getObject();
		if (photo!=null)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					photo.setRotation((photo.getRotation()+angle)%360);
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
