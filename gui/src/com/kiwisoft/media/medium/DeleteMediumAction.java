package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;

import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeleteMediumAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public DeleteMediumAction(ApplicationFrame frame)
	{
		super(Medium.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		final Medium medium=(Medium)getObject();
		if (medium.isUsed())
		{
			showMessageDialog(frame, "The video '"+medium.getName()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
			return;
		}
		int option=showConfirmDialog(frame, "Delete video '"+medium.getName()+"'?", "Confirmation", YES_NO_OPTION, QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				@Override
				public void run() throws Exception
				{
					MediumManager.getInstance().dropMedium(medium);
				}

				@Override
				public void handleError(Throwable throwable, boolean rollback)
				{
					showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
