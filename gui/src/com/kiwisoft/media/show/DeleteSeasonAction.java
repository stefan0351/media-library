package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.util.List;
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
 * Date: 16.03.2007
 * Time: 19:42:45
 * To change this template use File | Settings | File Templates.
 */
public class DeleteSeasonAction extends MultiContextAction<Season>
{
	private ApplicationFrame frame;

	public DeleteSeasonAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

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
