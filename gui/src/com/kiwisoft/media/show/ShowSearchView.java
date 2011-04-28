package com.kiwisoft.media.show;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.PinAction;
import com.kiwisoft.media.dataimport.SerienJunkiesDeLoaderAction;
import com.kiwisoft.media.dataimport.TVComLoaderAction;
import com.kiwisoft.media.dataimport.TVTVDeLoaderContextAction;
import com.kiwisoft.media.person.ShowCreditsAction;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchView;
import com.kiwisoft.swing.SearchController;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.StringUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShowSearchView extends SearchView<Show>
{
	@Override
	public String getTitle()
	{
		return "Shows";
	}

	@Override
	protected TableController<Show> createResultTable(final ApplicationFrame frame)
	{
		SortableTableModel<Show> tmShows=new DefaultSortableTableModel<Show>("title", "germanTitle", "type");

		return new TableController<Show>(tmShows, new DefaultTableConfiguration("shows.list", ShowSearchView.class, "shows"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new ShowDetailsAction());
				actions.add(new CreateShowAction());
				actions.add(new DeleteShowAction(frame));
				actions.add(new ShowSeasonsAction(frame));
				actions.add(new ShowEpisodesAction(frame));
				actions.add(new PinAction(getSearchController()));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				ComplexAction downloadAction=new ComplexAction("Download");
//				downloadAction.addAction(new ProSiebenDeLoaderAction(frame));
				downloadAction.addAction(new TVTVDeLoaderContextAction(frame));
				downloadAction.addSeparator();
				downloadAction.addAction(new TVComLoaderAction(frame));
				downloadAction.addAction(new SerienJunkiesDeLoaderAction(frame));

				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new ShowDetailsAction());
				actions.add(null);
				actions.add(new CreateShowAction());
				actions.add(new DeleteShowAction(frame));
				actions.add(null);
				actions.add(new ShowEpisodesAction(frame));
				actions.add(new ShowSeasonsAction(frame));
				actions.add(new ShowAirdatesAction(frame));
				actions.add(new ShowMoviesAction(frame));
				actions.add(new ShowCreditsAction(frame));
				actions.add(new ShowTracksAction(frame));
				actions.add(new ShowLinksAction(frame));
				actions.add(downloadAction);
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new ShowEpisodesAction(frame);
			}
		};
	}

	@Override
	protected SearchController<Show> createSearchController(TableController<Show> showTableController)
	{
		return new SearchController<Show>(showTableController)
		{
			@Override
			protected Set<Show> doSearch(String searchText)
			{
				if (StringUtils.isEmpty(searchText)) return ShowManager.getInstance().getShows();
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				Set<Show> shows=new HashSet<Show>();
				shows.addAll(DBLoader.getInstance().loadSet(Show.class, null,
															"title like ? or german_title like ? limit 1001",
															searchText, searchText));
				if (shows.size()<1001)
				{
					shows.addAll(DBLoader.getInstance().loadSet(Show.class, "names",
																"names.type=? and names.ref_id=shows.id and names.name like ?", Name.SHOW, searchText));
				}
				return shows;
			}

			@Override
			protected SortableTableRow<Show> createRow(Show show)
			{
				return new ShowTableRow(show);
			}
		};
	}

	@Override
	protected void installCollectionListener()
	{
		getModelListenerList().installPropertyChangeListener(ShowManager.getInstance(), new CollectionObserver(ShowManager.SHOWS));
		super.installCollectionListener();
	}

	private static class ShowTableRow extends SortableTableRow<Show> implements PropertyChangeListener
	{
		public ShowTableRow(Show show)
		{
			super(show);
		}

		@Override
		public void installListener()
		{
			try
			{
				getUserObject().addPropertyChangeListener(this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if ("title".equals(property)) return getUserObject().getTitle();
			if ("germanTitle".equals(property)) return getUserObject().getGermanTitle();
			if ("type".equals(property)) return StringUtils.formatAsEnumeration(getUserObject().getGenres(), ", ");
			return "";
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}
	}
}
