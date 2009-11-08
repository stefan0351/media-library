package com.kiwisoft.media.channel;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Channel;
import com.kiwisoft.media.ChannelManager;

/**
 * @author Stefan Stiller
 */
public class DeleteChannelAction extends SimpleContextAction
{
	private ApplicationFrame frame;

	public DeleteChannelAction(ApplicationFrame frame)
	{
		super(Channel.class, "Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Channel channel=(Channel)getObject();
		if (channel.isUsed())
		{
			JOptionPane.showMessageDialog(frame, "The channel '"+channel.getName()+"' can't be deleted.", "Message",
										  JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete channel '"+channel.getName()+"'?", "Confirmation",
												 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				ChannelManager.getInstance().dropChannel(channel);
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