package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Iterator;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.db.DBSession;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.03.2007
 * Time: 20:14:17
 * To change this template use File | Settings | File Templates.
 */
public class DeleteAirdateAction extends MultiContextAction<Airdate>
{
	private ApplicationFrame frame;

	public DeleteAirdateAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Airdate> airdates=getObjects();
		Iterator it=airdates.iterator();
		while (it.hasNext())
		{
			Airdate airdate=(Airdate)it.next();
			if (airdate.isUsed())
			{
				JOptionPane.showMessageDialog(frame,
											  "The airdates '"+airdate.getName()+"' can't be deleted.",
											  "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame,
												 "Really delete airdates?",
												 "Confirm",
												 JOptionPane.YES_NO_OPTION,
												 JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				it=airdates.iterator();
				while (it.hasNext())
				{
					Airdate airdate=(Airdate)it.next();
					airdate.delete();
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
