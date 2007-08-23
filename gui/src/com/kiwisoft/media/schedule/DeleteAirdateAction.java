package com.kiwisoft.media.schedule;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.gui.actions.MultiContextAction;

/**
 * @author Stefan Stiller
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
