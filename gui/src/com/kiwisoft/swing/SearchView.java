package com.kiwisoft.swing;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.Pinnable;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
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
public abstract class SearchView<T> extends ViewPanel implements Pinnable
{
	private TableController<T> tableController;
	private JLabel resultLabel;
	private boolean pinned;
	private Set<String> searches=new LinkedHashSet<String>();

	@Override
	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		tableController=createResultTable(frame);

		installCollectionListener();

		JTextField searchField=new JTextField();
		searchField.addActionListener(new SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		return panel;
	}

	protected void installCollectionListener()
	{
	}

	protected abstract TableController<T> createResultTable(ApplicationFrame frame);

	protected TableController<T> getTableController()
	{
		return tableController;
	}

	private void runSearch(String searchText, boolean pinned)
	{
		Set<T> resultSet=doSearch(searchText);
		SortableTableModel<T> tableModel=tableController.getModel();
		if (!pinned) tableModel.clear();
		List<SortableTableRow<T>> rows=new ArrayList<SortableTableRow<T>>(resultSet.size());
		for (T object : resultSet) rows.add(createRow(object));
		tableModel.addRows(rows);
		tableModel.sort();
		int rowCount=rows.size();
		if (rows.isEmpty()) resultLabel.setText("No rows found.");
		else if (rowCount==1) resultLabel.setText("1 row found.");
		else if (rowCount>1000) resultLabel.setText("More than 1000 Row(s) found.");
		else resultLabel.setText(rowCount+" rows found.");
		if (!pinned) searches.clear();
		searches.add(searchText);
	}

	protected abstract Set<T> doSearch(String searchText);

	protected abstract SortableTableRow<T> createRow(T object);

	@Override
	public boolean isPinned()
	{
		return pinned;
	}

	@Override
	public void setPinned(boolean b)
	{
		pinned=b;
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
		if (!searches.isEmpty()) title=title+": "+StringUtils.formatAsEnumeration(searches);
		Bookmark bookmark=new Bookmark(title, SearchView.class);
		bookmark.setParameter("viewClass", getClass().getName());
		bookmark.setParameter("pinned", String.valueOf(pinned));
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
			searchView.pinned=Boolean.valueOf(bookmark.getParameter("pinned"));
			searchView.createView(frame); // Make sure table controller is initialized
			int i=0;
			while (true)
			{
				final String searchText=bookmark.getParameter("search"+(i++));
				if (searchText==null) break;
				searchView.runSearch(searchText, true);
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
						if (!tableModel.containsObject(newObject)) tableModel.addRow(createRow(newObject));
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

	private class SearchActionListener implements ActionListener
	{
		private final JTextField searchField;

		public SearchActionListener(JTextField searchField)
		{
			this.searchField=searchField;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			runSearch(searchField.getText(), pinned);
		}
	}
}
