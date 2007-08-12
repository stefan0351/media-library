package com.kiwisoft.media.show;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.medium.CreateMediumAction;
import com.kiwisoft.utils.db.Chain;
import com.kiwisoft.utils.db.ChainEvent;
import com.kiwisoft.utils.db.ChainListener;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.*;

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

	public String getTitle()
	{
		if (season!=null)
			return show.getTitle()+" - "+season.getSeasonName()+" - Episodes";
		else
			return show.getTitle()+" - Episodes";
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		SortableTableModel<Episode> tmEpisodes=new DefaultSortableTableModel<Episode>("userkey", "title", "germanTitle", "firstAired");
		createTableData(tmEpisodes);

		tableController=new TableController<Episode>(tmEpisodes, new DefaultTableConfiguration(EpisodesView.class, "episodes"))
		{
			public List<ContextAction<? super Episode>> getToolBarActions()
			{
				List<ContextAction<? super Episode>> actions=new ArrayList<ContextAction<? super Episode>>();
				actions.add(new EpisodeDetailsAction());
				actions.add(new NewEpisodeAction(show));
				actions.add(new DeleteEpisodeAction(show, EpisodesView.this));
				if (season==null)
				{
					actions.add(new ChainMoveUpAction(this, show.getEpisodes()));
					actions.add(new ChainMoveDownAction(this, show.getEpisodes()));
				}
				return actions;
			}

			public List<ContextAction<? super Episode>> getContextActions()
			{
				List<ContextAction<? super Episode>> actions=new ArrayList<ContextAction<? super Episode>>();
				actions.add(new EpisodeDetailsAction());
				actions.add(null);
				actions.add(new NewEpisodeAction(show));
				actions.add(new DeleteEpisodeAction(show, EpisodesView.this));
				actions.add(null);
				actions.add(new CreateSeasonAction());
				actions.add(new CreateMediumAction());
				return actions;
			}

			public ContextAction<Episode> getDoubleClickAction()
			{
				return new EpisodeDetailsAction();
			}
		};
		return tableController.createComponent();
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
			tableModel.addRow(new EpisodeTableRow(episode));
		}
		tableModel.sort();
		show.addCollectionListener(collectionObserver);
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
		if (season==null) show.getEpisodes().removeChainListener(collectionObserver);
		show.removeCollectionListener(collectionObserver);
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener, ChainListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.EPISODES.equals(event.getPropertyName()))
			{
				SortableTableModel<Episode> tableModel=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						SortableTable table=tableController.getTable();
						Episode newEpisode=(Episode)event.getElement();
						EpisodeTableRow row=new EpisodeTableRow(newEpisode);
						int newIndex=tableModel.addRow(row);
						tableModel.sort();
						table.getSelectionModel().setSelectionInterval(newIndex, newIndex);
						table.scrollRectToVisible(table.getCellRect(newIndex, 0, false));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tableModel.indexOf(event.getElement());
						if (index>=0) tableModel.removeRowAt(index);
						break;
				}
			}
		}

		public void chainChanged(ChainEvent event)
		{
			switch (event.getType())
			{
				case ChainEvent.CHANGED:
					tableController.getModel().sort();
			}
		}
	}

	private static class EpisodeTableRow extends SortableTableRow<Episode> implements PropertyChangeListener
	{
		public EpisodeTableRow(Episode episode)
		{
			super(episode);
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

		public Comparable getSortValue(int column, String property)
		{
			if (column==0)
			{
				Episode episode=getUserObject();
				return episode.getChainPosition();
			}
			return super.getSortValue(column, property);
		}


		@Override
		public String getCellFormat(int column, String property)
		{
			if ("firstAired".equals(property)) return "Date only";
			return super.getCellFormat(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Episode episode=getUserObject();
			switch (column)
			{
				case 0:
					return episode.getUserKey();
				case 1:
					return episode.getTitle();
				case 2:
					return episode.getGermanTitle();
				case 3:
					return episode.getAirdate();
			}
			return "";
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

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
			frame.setCurrentView(new EpisodesView(season), true);
		}
		else
		{
			Long id=new Long(bookmark.getParameter("id"));
			Show show=ShowManager.getInstance().getShow(id);
			frame.setCurrentView(new EpisodesView(show), true);
		}
	}
}