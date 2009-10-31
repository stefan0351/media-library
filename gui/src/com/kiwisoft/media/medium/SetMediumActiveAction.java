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
public class SetMediumActiveAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public SetMediumActiveAction(ApplicationFrame frame)
	{
		super(Medium.class, "Set Active");
		this.frame=frame;
	}

	@Override
	protected boolean isValid(Object object)
	{
		return super.isValid(object) && ((Medium)object).isObsolete();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final List<Medium> mediums=getObjects();
		DBSession.execute(new Transactional()
		{
			@Override
			public void run() throws Exception
			{
				for (Medium medium : mediums)
				{
					medium.setObsolete(false);
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
