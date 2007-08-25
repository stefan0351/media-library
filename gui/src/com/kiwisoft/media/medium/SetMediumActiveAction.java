package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class SetMediumActiveAction extends MultiContextAction<Medium>
{
	private ApplicationFrame frame;

	public SetMediumActiveAction(ApplicationFrame frame)
	{
		super("Set Active");
		this.frame=frame;
	}

	@Override
	public void update(List<? extends Medium> objects)
	{
		super.update(objects);
		for (Medium medium : getObjects())
		{
			if (!medium.isObsolete())
			{
				setEnabled(false);
				break;
			}
		}
	}

	public void actionPerformed(ActionEvent event)
	{
		final List<Medium> mediums=getObjects();
		DBSession.execute(new Transactional()
		{
			public void run() throws Exception
			{
				for (Medium medium : mediums)
				{
					medium.setObsolete(false);
				}
			}

			public void handleError(Throwable throwable, boolean rollback)
			{
				JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
