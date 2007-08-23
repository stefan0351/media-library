package com.kiwisoft.media.medium;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.MultiContextAction;

/**
 * @author Stefan Stiller
 */
public class DeleteTrackAction extends MultiContextAction<Track>
{
	private ApplicationFrame frame;
	private Medium medium;

	public DeleteTrackAction(ApplicationFrame frame, Medium video)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
		this.medium=video;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Track> tracks=getObjects();
		Iterator it=tracks.iterator();
		while (it.hasNext())
		{
			Track track=(Track)it.next();
			if (track.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "The track '"+track.getEvent()+"' can't be deleted.", "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete tracks?", "Confirmation",
												 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				it=tracks.iterator();
				while (it.hasNext())
				{
					Track track=(Track)it.next();
					medium.dropTrack(track);
				}
				transaction.close();
			}
			catch (Exception e1)
			{
				try
				{
					if (transaction!=null) transaction.rollback();
				}
				catch (SQLException e2)
				{
					e2.printStackTrace();
				}
				e1.printStackTrace();
				JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}