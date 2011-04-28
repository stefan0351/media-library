package com.kiwisoft.media.fanfic;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchController;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class FanFicsSearchView extends FanFicsView
{
	private SearchController<FanFic> searchController;

	public FanFicsSearchView()
	{
		super(null);
	}

	@Override
	public String getTitle()
	{
		return "Fan Fiction - Search";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		JComponent mainComponent=super.createContentPanel(frame);

		searchController=new SearchController<FanFic>(getTableController())
		{
			@Override
			protected Set<FanFic> doSearch(String searchText)
			{
				if (StringUtils.isEmpty(searchText)) return DBLoader.getInstance().loadSet(FanFic.class, null, "limit 1001");
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				Set<FanFic> fanFics=new HashSet<FanFic>();
				fanFics.addAll(DBLoader.getInstance().loadSet(FanFic.class, null,
															  "title like ? limit 1001",
															  searchText));
				return fanFics;
			}

			@Override
			protected SortableTableRow<FanFic> createRow(FanFic object)
			{
				return new FanFicTableRow(object);
			}
		};

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchController.getSearchField(), BorderLayout.NORTH);
		panel.add(mainComponent, BorderLayout.CENTER);
		panel.add(searchController.getResultLabel(), BorderLayout.SOUTH);

		return panel;
	}

	@Override
	public Bookmark getBookmark()
	{
		String title=getTitle();
		Set<String> searches=searchController.getSearches();
		if (!searches.isEmpty()) title=title+": "+StringUtils.formatAsEnumeration(searches);
		Bookmark bookmark=new Bookmark(title, FanFicsSearchView.class);
		bookmark.setParameter("pinned", String.valueOf(searchController.isPinned()));
		int i=0;
		for (String search : searches) bookmark.setParameter("search"+(i++), search);
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		try
		{
			FanFicsSearchView searchView=new FanFicsSearchView();
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

	public void addFanFic(FanFic result)
	{
		SortableTableModel<FanFic> model=searchController.getModel();
		if (!model.containsObject(result)) searchController.addRow(result);
	}
}