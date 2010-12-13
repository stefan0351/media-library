package com.kiwisoft.swing;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.Pinnable;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.BeanTableRow;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.collection.CollectionChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 24.10.2009
 */
public abstract class SearchView<T> extends ViewPanel
{
	private TableController<T> tableController;
	private SearchController<T> searchController;

	@Override
	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		tableController=createResultTable(frame);

		installCollectionListener();

		searchController=createSearchController(tableController);

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchController.getSearchField(), BorderLayout.NORTH);
		panel.add(tableController.getComponent(), BorderLayout.CENTER);
		panel.add(searchController.getResultLabel(), BorderLayout.SOUTH);

		return panel;
	}

	protected abstract TableController<T> createResultTable(ApplicationFrame frame);

	protected abstract SearchController<T> createSearchController(TableController<T> tableController);

	protected void installCollectionListener()
	{
	}

	protected TableController<T> getTableController()
	{
		return tableController;
	}

	protected SearchController<T> getSearchController()
	{
		return searchController;
	}

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		String title=getTitle();
		Set<String> searches=searchController.getSearches();
		if (!searches.isEmpty()) title=title+": "+StringUtils.formatAsEnumeration(searches);
		Bookmark bookmark=new Bookmark(title, SearchView.class);
		bookmark.setParameter("viewClass", getClass().getName());
		bookmark.setParameter("pinned", String.valueOf(searchController.isPinned()));
		int i=0;
		for (String search : searches) bookmark.setParameter("search"+(i++), search);
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		try
		{
			String viewClassName=bookmark.getParameter("viewClass");
			Class<? extends SearchView> viewClass=Utils.cast(Class.forName(viewClassName));
			SearchView searchView=viewClass.newInstance();
			searchView.createView(frame); // Make sure table controller is initialized
			int i=0;
			while (true)
			{
				final String searchText=bookmark.getParameter("search"+(i++));
				if (searchText==null) break;
				searchView.searchController.setPinned(Boolean.valueOf(bookmark.getParameter("pinned")));
				searchView.searchController.runSearch(searchText, true);
			}
			frame.setCurrentView(searchView);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
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

	protected class CollectionObserver implements CollectionChangeListener
	{
		private String property;

		public CollectionObserver(String property)
		{
			this.property=property;
		}

		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (property.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						T newObject=Utils.<T>cast(event.getElement());
						SortableTableModel<T> tableModel=tableController.getModel();
						if (!tableModel.containsObject(newObject)) tableModel.addRow(searchController.createRow(newObject));
						break;
					case CollectionChangeEvent.REMOVED:
						SortableTableModel<T> model=tableController.getModel();
						int index=model.indexOf(event.getElement());
						if (index>=0) model.removeRowAt(index);
						break;
				}
			}
		}
	}
}
