package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeleteDomainAction extends SimpleContextAction<FanDom>
{
	private ApplicationFrame frame;

	public DeleteDomainAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		FanDom domain=getObject();
		if (domain.isUsed())
		{
			JOptionPane.showMessageDialog(frame, "Domain '"+domain.getName()+"' can't be deleted.", "Message",
										  JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete domain '"+domain.getName()+"'?", "Confirmation",
												 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				FanFicManager.getInstance().dropDomain(domain);
				transaction.close();
			}
			catch (Exception e)
			{
				if (transaction!=null)
				{
					try
					{
						transaction.rollback();
					}
					catch (SQLException e1)
					{
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
