package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.MultiContextAction;

/**
 * @author Stefan Stiller
 */
public class SetVideoActiveAction extends MultiContextAction<Video>
{
	private ApplicationFrame frame;

	public SetVideoActiveAction(ApplicationFrame frame)
	{
		super("Set Active");
		this.frame=frame;
	}

	@Override
	public void update(List<Video> objects)
	{
		super.update(objects);
		for (Video video : getObjects())
		{
			if (!video.isObsolete())
			{
				setEnabled(false);
				break;
			}
		}
	}

	public void actionPerformed(ActionEvent event)
	{
		final List<Video> videos=getObjects();
		DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				for (Video video : videos)
				{
					video.setObsolete(false);
				}
			}

			public void handleError(Throwable throwable)
			{
				JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
