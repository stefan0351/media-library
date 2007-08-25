package com.kiwisoft.media.person;

import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.swing.actions.MultiContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.persistence.Transaction;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeleteCastAction extends MultiContextAction<CastMember>
{
	private Show show;
	private ApplicationFrame frame;

	public DeleteCastAction(Show show, ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.show=show;
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<CastMember> casts=getObjects();
		for (CastMember cast : casts)
		{
			if (cast.isUsed())
			{
				JOptionPane.showMessageDialog(frame, "Cast '"+cast+"' can't be deleted.", "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame, "Delete cast?", "Confirm", JOptionPane.YES_NO_OPTION,
												 JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (CastMember cast : casts) show.dropCast(cast);
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
