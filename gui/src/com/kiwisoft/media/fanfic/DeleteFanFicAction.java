package com.kiwisoft.media.fanfic;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeleteFanFicAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public DeleteFanFicAction(ApplicationFrame frame)
	{
		super(FanFic.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		FanFic fanFic=(FanFic)getObject();
		if (fanFic.isUsed())
		{
			JOptionPane.showMessageDialog(frame, "FanFic '"+fanFic.getTitle()+"' can't be deleted.", "Message",
										  JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete fanfic '"+fanFic.getTitle()+"'?", "Confirmation",
												 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				FanFicManager.getInstance().dropFanFic(fanFic);
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
