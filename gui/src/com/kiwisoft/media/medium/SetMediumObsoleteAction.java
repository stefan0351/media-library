package com.kiwisoft.media.medium;

import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class SetMediumObsoleteAction extends MultiContextAction<Medium>
{
	private ApplicationFrame frame;

	public SetMediumObsoleteAction(ApplicationFrame frame)
	{
		super("Set Obsolete");
		this.frame=frame;
	}

	@Override
	public void update(List<? extends Medium> objects)
	{
		super.update(objects);
		for (Medium medium : getObjects())
		{
			if (medium.isObsolete())
			{
				setEnabled(false);
				break;
			}
		}
	}

	public void actionPerformed(ActionEvent event)
	{
		final List<Medium> media=getObjects();
		DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				for (Medium video : media)
				{
					video.setObsolete(true);
				}
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
