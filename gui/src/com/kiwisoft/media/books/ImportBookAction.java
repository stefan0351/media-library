package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataImport.AmazonDeLoader;
import com.kiwisoft.media.dataImport.BookData;
import com.kiwisoft.swing.actions.ContextAction;

/**
 * @author Stefan Stiller
 */
public class ImportBookAction extends ContextAction<Book>
{
	private ApplicationFrame frame;

	public ImportBookAction(ApplicationFrame frame)
	{
		super("Import");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		String url=JOptionPane.showInputDialog(frame, "URL:", "Load Book", JOptionPane.QUESTION_MESSAGE);
		if (url!=null)
		{
			AmazonDeLoader loader=new AmazonDeLoader(url);
			try
			{
				BookData bookData=loader.load();
				System.out.println("bookData = "+bookData);
				BookDataDetailsView.createDialog(frame, bookData);
			}
			catch (IOException e1)
			{
				JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
