package com.kiwisoft.media.books;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.*;

import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultSortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.media.utils.TableController;

public class BooksView extends ViewPanel
{
	private TableController<Book> tableController;
	private JLabel resultLabel;
	private JTextField searchField;

	public BooksView()
	{
	}

	public String getName()
	{
		return "Books";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Book> tableModel=new DefaultSortableTableModel<Book>("title", "author", "publisher", "binding", "pageCount", "isbn");

		tableController=new TableController<Book>(tableModel, new DefaultTableConfiguration(BooksView.class, "books"))
		{
			@Override
			public List<ContextAction<? super Book>> getToolBarActions()
			{
				List<ContextAction<? super Book>> actions=new ArrayList<ContextAction<? super Book>>(1);
				actions.add(new BookDetailsAction());
				actions.add(new NewBookAction());
				actions.add(new DeleteBookAction(frame));
				actions.add(new ImportBookAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Book>> getContextActions()
			{
				List<ContextAction<? super Book>> actions=new ArrayList<ContextAction<? super Book>>();
				actions.add(new BookDetailsAction());
				actions.add(null);
				actions.add(new NewBookAction());
				actions.add(new DeleteBookAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Book> getDoubleClickAction()
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

		getModelListenerList().installCollectionListener(BookManager.getInstance(), new CollectionChangeObserver());

		return panel;
	}

	protected void installComponentListeners()
	{
		tableController.installListeners();
		super.installComponentListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
		super.removeComponentListeners();
	}

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
						model.addRow(new MyRow(book));
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

	private static class MyRow extends SortableTableRow<Book> implements PropertyChangeListener
	{
		public MyRow(Book book)
		{
			super(book);
		}

		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Object getDisplayValue(int column, String property)
		{
			if ("title".equals(property)) return getUserObject().getTitle();
			if ("author".equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getAuthors(), "; ");
			else if ("publisher".equals(property)) return getUserObject().getPublisher();
			else if ("pageCount".equals(property)) return getUserObject().getPageCount();
			else if ("binding".equals(property)) return getUserObject().getBinding();
			else if ("isbn".equals(property))
			{
				String isbn=getUserObject().getIsbn13();
				if (StringUtils.isEmpty(isbn)) isbn=getUserObject().getIsbn10();
				return isbn;
			}
			return null;
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), BooksView.class);
		String searchText=searchField.getText();
		if (!StringUtils.isEmpty(searchText))
		{
			bookmark.setName(getName()+": "+searchText);
			bookmark.setParameter("searchText", searchText);
		}
		return bookmark;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		final BooksView view=new BooksView();
		frame.setCurrentView(view, true);
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
			List<MyRow> rows=new ArrayList<MyRow>(books.size());
			for (Book book : books) rows.add(new MyRow(book));
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
