package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.MultiContextAction;

/**
 * @author Stefan Stiller
 */
public class DeleteSeasonAction extends MultiContextAction
{
	private ApplicationFrame frame;

	public DeleteSeasonAction(ApplicationFrame frame)
	{
		super(Season.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Season> seasons=getObjects();
		for (Season season : seasons)
		{
			if (season.isUsed())
			{
				JOptionPane.showMessageDialog(frame,
											  "The season '"+season.getSeasonName()+"' can't be deleted.",
											  "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame,
												 "Really delete seasons?",
												 "Delete?",
												 JOptionPane.YES_NO_OPTION,
												 JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (Season season : seasons) season.getShow().dropSeason(season);
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
