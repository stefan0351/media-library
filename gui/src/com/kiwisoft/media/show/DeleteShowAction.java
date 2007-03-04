package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transactional;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 21:43:26
 * To change this template use File | Settings | File Templates.
 */
public class DeleteShowAction extends SimpleContextAction<Show>
{
	private ApplicationFrame frame;

	public DeleteShowAction(ApplicationFrame frame)
	{
		super("Löschen");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		final Show show=getObject();
		if (show.isUsed())
		{
			JOptionPane.showMessageDialog(frame,
										  "Die Serie '"+show.getName()+"' kann nicht gelöscht werden.",
										  "Meldung",
										  JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int option=JOptionPane.showConfirmDialog(frame,
												 "Serie '"+show.getName()+"' wirklick löschen?",
												 "Löschen?",
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

				public void handleError(Throwable throwable)
				{
					JOptionPane.showMessageDialog(frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
}
