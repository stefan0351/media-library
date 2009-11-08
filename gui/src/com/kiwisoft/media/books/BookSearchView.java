package com.kiwisoft.media.books;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.PinAction;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchView;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookSearchView extends SearchView<Book>
{
	@Override
	public String getTitle()
	{
		return "Books";
	}

	@Override
	protected TableController<Book> createResultTable(final ApplicationFrame frame)
	{
		SortableTableModel<Book> tableModel=new DefaultSortableTableModel<Book>("title", "series", "author", "isbn");
		return new TableController<Book>(tableModel, new DefaultTableConfiguration("books.list", BookSearchView.class, "books"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(1);
				actions.add(new BookDetailsAction());
				actions.add(new NewBookAction());
				actions.add(new DeleteBookAction(frame));
				actions.add(new ImportBookAction(frame));
				actions.add(new PinAction(BookSearchView.this));
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
	}

	@Override
	protected SortableTableRow<Book> createRow(Book object)
	{
		return new BookTableRow(object);
	}

	@Override
	protected void installCollectionListener()
	{
		getModelListenerList().addDisposable(BookManager.getInstance().addCollectionListener(new CollectionObserver(BookManager.BOOKS)));
		super.installCollectionListener();
	}

	@Override
	protected Set<Book> doSearch(String searchText)
	{
		if (StringUtils.isEmpty(searchText)) return BookManager.getInstance().getBooks();
		if (searchText.contains("*")) searchText=searchText.replace('*', '%');
		else searchText="%"+searchText+"%";
		Set<Book> books=new HashSet<Book>();
		books.addAll(DBLoader.getInstance().loadSet(Book.class, null, "(title like ? or series_name like ?) limit 1001", searchText, searchText));
		if (books.size()<1001)
		{
			books.addAll(DBLoader.getInstance().loadSet(Book.class,
														"persons p, map_book_author m",
														"m.book_id=books.id and m.author_id=p.id and name like ? limit 1001",
														searchText));
		}
		return books;
	}
}
