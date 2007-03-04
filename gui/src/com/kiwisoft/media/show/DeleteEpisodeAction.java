package com.kiwisoft.media.show;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Show;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Gieselbert
 * Date: 03.03.2007
 * Time: 18:38:11
 * To change this template use File | Settings | File Templates.
 */
public class DeleteEpisodeAction extends MultiContextAction<Episode>
{
	private Show show;
	private ViewPanel viewPanel;

	public DeleteEpisodeAction(Show show, ViewPanel viewPanel)
	{
		super("Löschen");
		this.show=show;
		this.viewPanel=viewPanel;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Episode> episodes=getObjects();
		for (Episode episode : episodes)
		{
			if (episode.isUsed())
			{
				JOptionPane.showMessageDialog(viewPanel,
						"Die Folge '"+episode.getName()+"' kann nicht gelöscht werden.",
						"Meldung",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(viewPanel,
												 "Episoden wirklick löschen?",
												 "Löschen?",
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
