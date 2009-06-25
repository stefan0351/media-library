package com.kiwisoft.media.books;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;

public class BooksView extends ViewPanel
{
	private TableController<Book> tableController;
	private JLabel resultLabel;
	private JTextField searchField;

	public BooksView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Books";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Book> tableModel=new DefaultSortableTableModel<Book>("title", "author", "publisher", "binding", "pageCount", "isbn");

		tableController=new TableController<Book>(tableModel, new DefaultTableConfiguration("books.list", BooksView.class, "books"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(1);
				actions.add(new BookDetailsAction());
				actions.add(new NewBookAction());
				actions.add(new DeleteBookAction(frame));
				actions.add(new ImportBookAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new BookDetailsAction());
				actions.add(null);
				actions.add(new NewBookAction());
				actions.add(new DeleteBookAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new BookDetailsAction();
			}
		};

		searchField=new JTextField();
		searchField.addActionListener(new SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		getModelListenerList().addDisposable(BookManager.getInstance().addCollectionListener(new CollectionChangeObserver()));

		return panel;
	}

	@Override
	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	@Override
	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

	@Override
	public void dispose()
	{
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (BookManager.BOOKS.equals(event.getPropertyName()))
			{
				SortableTableModel<Book> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Book book=(Book)event.getElement();
						model.addRow(new BookTableRow(book));
						model.sort();
						break;
					case CollectionChangeEvent.REMOVED:
						int index=model.indexOf(event.getElement());
						if (index>=0) model.removeRowAt(index);
						break;
				}
			}
		}
	}

    @Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), BooksView.class);
		String searchText=searchField.getText();
		if (!StringUtils.isEmpty(searchText))
		{
			bookmark.setName(getTitle()+": "+searchText);
			bookmark.setParameter("searchText", searchText);
		}
		return bookmark;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		final BooksView view=new BooksView();
		frame.setCurrentView(view);
		final String searchText=bookmark.getParameter("searchText");
		if (!StringUtils.isEmpty(searchText))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					view.searchField.setText(searchText);
					view.searchField.postActionEvent();
				}
			});
		}
	}

	private class SearchActionListener implements ActionListener
	{
		private final JTextField searchField;

		public SearchActionListener(JTextField searchField)
		{
			this.searchField=searchField;
		}

		public void actionPerformed(ActionEvent e)
		{
			String searchText=searchField.getText();

			Set<Book> books;
			if (StringUtils.isEmpty(searchText)) books=BookManager.getInstance().getBooks();
			else
			{
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				books=new HashSet<Book>();
				books.addAll(DBLoader.getInstance().loadSet(Book.class, null, "title like ? limit 1001", searchText));
				if (books.size()<1001)
				{
					books.addAll(DBLoader.getInstance().loadSet(Book.class,
																"persons p, map_book_author m",
																"m.book_id=books.id and m.author_id=p.id and name like ? limit 1001",
																searchText));
				}
			}
			SortableTableModel<Book> tableModel=tableController.getModel();
			tableModel.clear();
			List<BookTableRow> rows=new ArrayList<BookTableRow>(books.size());
			for (Book book : books) rows.add(new BookTableRow(book));
			tableModel.addRows(rows);
			tableModel.sort();
			int rowCount=rows.size();
			if (rows.isEmpty()) resultLabel.setText("No books found.");
			else if (rowCount==1) resultLabel.setText("1 book found.");
			else if (rowCount>1000) resultLabel.setText("More than 1000 books found.");
			else resultLabel.setText(rowCount+" books found.");
		}
	}
}
