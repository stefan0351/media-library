package com.kiwisoft.media.movie;

import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.kiwisoft.utils.gui.actions.MultiContextAction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.Icons;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.media.show.Show;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 10.03.2007
 * Time: 21:53:26
 * To change this template use File | Settings | File Templates.
 */
public class DeleteMovieAction extends MultiContextAction<Movie>
{
	private ApplicationFrame frame;
	private Show show;

	public DeleteMovieAction(ApplicationFrame frame, Show show)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
		this.show=show;
	}

	public void actionPerformed(ActionEvent e)
	{
		List<Movie> movies=getObjects();
		for (Movie movie : movies)
		{
			if (movie.isUsed())
			{
				JOptionPane.showMessageDialog(frame,
											  "The movie '"+movie.getTitle()+"' can't be deleted.",
											  "Message",
											  JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		int option=JOptionPane.showConfirmDialog(frame,
												 "Delete movies",
												 "Confirmation",
												 JOptionPane.YES_NO_OPTION,
												 JOptionPane.QUESTION_MESSAGE);
		if (option==JOptionPane.YES_OPTION)
		{
			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				for (Movie movie : movies)
				{
					if (show!=null)
						show.dropMovie(movie);
					else
						MovieManager.getInstance().dropMovie(movie);
				}
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