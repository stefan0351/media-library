package com.kiwisoft.media.video;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Iterator;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.db.DBSession;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.03.2007
 * Time: 11:39:58
 * To change this template use File | Settings | File Templates.
 */
public class DeleteRecordAction extends MultiContextAction<Recording>
{
	private ApplicationFrame frame;
	private Video video;

	public DeleteRecordAction(ApplicationFrame frame, Video video)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
		this.video=video;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Recording> records=getObjects();
		Iterator it=records.iterator();
		while (it.hasNext())
		{
			Recording recording=(Recording)it.next();
			if (recording.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "The record '"+recording.getEvent()+"' can't be deleted.", "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete records?", "Confirmation",
												 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				it=records.iterator();
				while (it.hasNext())
				{
					Recording record=(Recording)it.next();
					video.dropRecording(record);
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
