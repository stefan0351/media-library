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
public class SetMediumObsoleteAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public SetMediumObsoleteAction(ApplicationFrame frame)
	{
		super(Medium.class, "Set Obsolete");
		this.frame=frame;
	}


	@Override
	protected boolean isValid(Object object)
	{
		return super.isValid(object) && !((Medium)object).isObsolete();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final List<Medium> media=getObjects();
		DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				for (Medium video : media)
				{
					video.setObsolete(true);
				}
			}

			@Override
			public void handleError(Throwable throwable, boolean rollback)
			{
				JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
