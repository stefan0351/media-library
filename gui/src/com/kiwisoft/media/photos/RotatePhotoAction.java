package com.kiwisoft.media.photos;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;

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

				public void handleError(Throwable throwable)
				{
					JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
