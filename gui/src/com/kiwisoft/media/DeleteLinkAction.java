package com.kiwisoft.media;

import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeleteLinkAction extends MultiContextAction<Link>
{
	private ApplicationFrame frame;
	private Show show;

	public DeleteLinkAction(ApplicationFrame frame, Show show)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
		this.show=show;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Link> links=getObjects();
		for (Link link : links)
		{
			if (link.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "The link '"+link.getName()+"' can't be deleted.", "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete links?", "Confirmation",
												 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (Link link : links) show.dropLink(link);
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
