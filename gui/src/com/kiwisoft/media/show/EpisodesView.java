package com.kiwisoft.media.show;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.ChainEvent;
import com.kiwisoft.collection.Chain;
import com.kiwisoft.media.medium.CreateMediumAction;
import com.kiwisoft.media.person.ShowCreditsAction;
import com.kiwisoft.media.dataimport.EpisodeData;
import com.kiwisoft.collection.ChainListener;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class EpisodesView extends ViewPanel
{
	private Show show;
	private Season season;

	// Dates Panel
	private CollectionChangeObserver collectionObserver;
	private TableController<Episode> tableController;

	public EpisodesView(Show show)
	{
		this.show=show;
	}

	public EpisodesView(Season season)
	{
		this.season=season;
		this.show=season.getShow();
	}

	@Override
	public String getTitle()
	{
		if (season!=null)
			return show.getTitle()+" - "+season.getSeasonName()+" - Episodes";
		else
			return show.getTitle()+" - Episodes";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Episode> tmEpisodes=new DefaultSortableTableModel<Episode>(
				Episode.USER_KEY, EpisodeData.TITLE, EpisodeData.GERMAN_TITLE, Episode.AIRDATE);
		createTableData(tmEpisodes);

		tableController=new TableController<Episode>(tmEpisodes, new DefaultTableConfiguration("episodes.list", EpisodesView.class, "episodes"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new EpisodeDetailsAction(frame));
				actions.add(new NewEpisodeAction(show));
				actions.add(new DeleteEpisodeAction(show, EpisodesView.this));
				if (season==null)
				{
					actions.add(new ChainMoveUpAction(this, show.getEpisodes()));
					actions.add(new ChainMoveDownAction(this, show.getEpisodes()));
				}
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new EpisodeDetailsAction(frame));
				actions.add(null);
				actions.add(new NewEpisodeAction(show));
				actions.add(new DeleteEpisodeAction(show, EpisodesView.this));
				actions.add(null);
				actions.add(new CreateSeasonAction());
				actions.add(new CreateMediumAction());
				actions.add(null);
				actions.add(new ShowCreditsAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new EpisodeDetailsAction(frame);
			}
		};
		return tableController.getComponent();
	}

	private void createTableData(SortableTableModel<Episode> tableModel)
	{
		collectionObserver=new CollectionChangeObserver();
		Iterator it;
		if (season!=null)
			it=season.getEpisodes().iterator();
		else
		{
			Chain<Episode> episodes=show.getEpisodes();
			it=episodes.iterator();
			episodes.addChainListener(collectionObserver);
		}
		while (it.hasNext())
		{
			Episode episode=(Episode)it.next();
			tableModel.addRow(new EpisodeRow(episode));
		}
		tableModel.sort();
		getModelListenerList().installPropertyChangeListener(show, collectionObserver);
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
		if (season==null) show.getEpisodes().removeChainListener(collectionObserver);
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver extends CollectionPropertyChangeAdapter implements ChainListener<Episode>
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (Show.EPISODES.equals(event.getPropertyName()))
			{
				SortableTableModel<Episode> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionPropertyChangeEvent.ADDED:
						SortableTable table=tableController.getTable();
						Episode newEpisode=(Episode)event.getElement();
						EpisodeRow row=new EpisodeRow(newEpisode);
						int newIndex=tableModel.addRow(row);
						tableModel.sort();
						table.getSelectionModel().setSelectionInterval(newIndex, newIndex);
						table.scrollRectToVisible(table.getCellRect(newIndex, 0, false));
						break;
					case CollectionPropertyChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
		}

		@Override
		public void chainChanged(ChainEvent event)
		{
			switch (event.getType())
			{
				case ChainEvent.CHANGED:
					tableController.getModel().sort();
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
		Bookmark bookmark=new Bookmark(getTitle(), EpisodesView.class);
		if (season!=null)
		{
			bookmark.setParameter("type", "season");
			bookmark.setParameter("id", String.valueOf(season.getId()));
		}
		else
		{
			bookmark.setParameter("type", "show");
			bookmark.setParameter("id", String.valueOf(show.getId()));
		}
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String type=bookmark.getParameter("type");
		if ("season".equals(type))
		{
			Long id=new Long(bookmark.getParameter("id"));
			Season season=ShowManager.getInstance().getSeason(id);
			frame.setCurrentView(new EpisodesView(season));
		}
		else
		{
			Long id=new Long(bookmark.getParameter("id"));
			Show show=ShowManager.getInstance().getShow(id);
			frame.setCurrentView(new EpisodesView(show));
		}
	}
}