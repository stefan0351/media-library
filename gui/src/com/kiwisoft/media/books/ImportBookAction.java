package com.kiwisoft.media.books;

import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import javax.swing.JOptionPane;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.dataimport.BookData;
import com.kiwisoft.media.dataimport.AmazonDeLoader;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.GuiUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;

/**
 * @author Stefan Stiller
 */
public class ImportBookAction extends ContextAction
{
	private ApplicationFrame frame;

	public ImportBookAction(ApplicationFrame frame)
	{
		super("Import");
		this.frame=frame;
	}

	public void actionPerformed(ActionEvent e)
	{
		String isbn=JOptionPane.showInputDialog(frame, "ISBN:", "Load Book", JOptionPane.QUESTION_MESSAGE);
		if (isbn!=null)
		{
			AmazonDeLoader loader=new AmazonDeLoader(isbn);
			try
			{
				BookData bookData=loader.load();
                Book book=null;
                Set<Book> books=findExistingBooks(bookData);
                if (!books.isEmpty())
                {
                    BookUpdateDialog dialog=new BookUpdateDialog(frame, books);
                    dialog.setVisible(true);
                    switch (dialog.getOption())
                    {
                        case BookUpdateDialog.CANCEL_OPTION:
                            return;
                        case BookUpdateDialog.UPDATE_OPTION:
                            book=dialog.getBook();
                            break;
                    }
                }
                BookDataDetailsView.createDialog(frame, bookData, book);
			}
			catch (Exception e1)
			{
                GuiUtils.handleThrowable(frame, e1);
			}
		}
	}

    private Set<Book> findExistingBooks(BookData bookData)
    {
        Set<Book> existingBooks=new HashSet<Book>();
        if (!StringUtils.isEmpty(bookData.getIsbn10()))
        {
            existingBooks.addAll(DBLoader.getInstance().loadSet(Book.class, null, "replace(replace(ISBN10, ' ', ''), '-', '')=?", BookManager.filterIsbn(bookData.getIsbn10())));
        }
        if (!StringUtils.isEmpty(bookData.getIsbn13()))
        {
            existingBooks.addAll(DBLoader.getInstance().loadSet(Book.class, null, "replace(replace(ISBN13, ' ', ''), '-', '')=?", BookManager.filterIsbn(bookData.getIsbn13())));
        }
        if (!StringUtils.isEmpty(bookData.getTitle()))
        {
            Set<Book> books=DBLoader.getInstance().loadSet(Book.class, null, "TITLE=?", bookData.getTitle());
            // Check if the book is also from the same authors
            for (Book book : books)
            {
                Set<String> authors=new HashSet<String>();
                for (Person author : book.getAuthors()) authors.add(author.getName());
                if (!Collections.disjoint(authors, bookData.getAuthors())) existingBooks.add(book);
            }
        }
        return existingBooks;
    }
}
