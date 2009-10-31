package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transaction;

/**
 * @author Stefan Stiller
 */
public class DeleteEpisodeAction extends MultiContextAction
{
	private Show show;
	private ViewPanel viewPanel;

	public DeleteEpisodeAction(Show show, ViewPanel viewPanel)
	{
		super(Episode.class, "Delete", Icons.getIcon("delete"));
		this.show=show;
		this.viewPanel=viewPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Episode> episodes=getObjects();
		for (Episode episode : episodes)
		{
			if (episode.isUsed())
			{
				JOptionPane.showMessageDialog(viewPanel,
											  "The episode '"+episode.getTitle()+"' can't be deleted.",
											  "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(viewPanel,
												 "Should episodes really be deleted?",
												 "Delete?",
												 JOptionPane.YES_NO_OPTION,
												 JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (Episode episode : episodes) show.dropEpisode(episode);
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
				JOptionPane.showMessageDialog(viewPanel, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
