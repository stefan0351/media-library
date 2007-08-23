package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
 */
public class DeleteShowAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public DeleteShowAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		final Show show=getObject();
		if (show.isUsed())
		{
			JOptionPane.showMessageDialog(frame,
										  "The show '"+show.getTitle()+"' can't be deleted.",
										  "Message",
										  JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int option=JOptionPane.showConfirmDialog(frame,
												 "Should show '"+show.getTitle()+"' really be deleted?",
												 "Delete?",
												 JOptionPane.YES_NO_OPTION,
												 JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					ShowManager.getInstance().dropShow(show);
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
