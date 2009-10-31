package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.show.Production;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;

/**
 * @author Stefan Stiller
 */
public class DeleteCreditsAction extends MultiContextAction
{
	private ApplicationFrame frame;
	private Production production;

	public DeleteCreditsAction(Production production, ApplicationFrame frame)
	{
		super(Credit.class, "Delete", Icons.getIcon("delete"));
		this.production=production;
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Credit> credits=getObjects();
		for (Credit credit : credits)
		{
			if (credit.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "Credit '"+credit.getPerson()+"' can't be deleted.", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete credit?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (Credit credit : credits) production.dropCredit(credit);
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
