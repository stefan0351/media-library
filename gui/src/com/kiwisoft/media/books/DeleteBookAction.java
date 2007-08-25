package com.kiwisoft.media.books;

import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showConfirmDialog;
import java.awt.event.ActionEvent;

import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.swing.actions.SimpleContextAction;
import com.kiwisoft.swing.icons.Icons;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.app.ApplicationFrame;

/**
 * @author Stefan Stiller
 */
public class DeleteBookAction extends SimpleContextAction<Book>
{
	private ApplicationFrame frame;

	public DeleteBookAction(ApplicationFrame frame)
	{
		super("Delete", Icons.getIcon("delete"));
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent event)
	{
		final Book book=getObject();
		if (book.isUsed())
		{
			showMessageDialog(frame, "The book '"+book.getTitle()+"' can't be deleted.", "Message", INFORMATION_MESSAGE);
			return;
		}
		int option=
			showConfirmDialog(frame, "Delete book '"+book.getTitle()+"'?", "Confirmation", YES_NO_OPTION, QUESTION_MESSAGE);
		if (option==YES_OPTION)
		{
			DBSession.execute(new Transactional()
			{
				public void run() throws Exception
				{
					BookManager.getInstance().dropBook(book);
				}

				public void handleError(Throwable throwable, boolean rollback)
				{
					GuiUtils.handleThrowable(frame, throwable);
				}
			});
		}
	}
}
