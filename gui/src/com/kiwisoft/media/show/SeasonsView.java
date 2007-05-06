package com.kiwisoft.media.show;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.actions.SimpleContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.DefaultSortableTableModel;

public class SeasonsView extends ViewPanel
{
	// Dates Panel
	private Show show;
	private CollectionChangeObserver collectionObserver;
	private TableController<Season> tableController;

	public SeasonsView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getTitle()+" - Seasons";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Season> tableModel=new DefaultSortableTableModel<Season>("name", "years");
		createTableData(tableModel);

		tableController=new TableController<Season>(tableModel, new MediaTableConfiguration("table.seasons"))
		{
			@Override
			public List<ContextAction<? super Season>> getToolBarActions()
			{
				List<ContextAction<? super Season>> actions=new ArrayList<ContextAction<? super Season>>(3);
				actions.add(new SeasonDetailsAction());
				actions.add(new NewSeasonAction(show));
				actions.add(new DeleteSeasonAction(frame));
				actions.add(new SeasonEpisodesAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Season>> getContextActions()
			{
				List<ContextAction<? super Season>> actions=new ArrayList<ContextAction<? super Season>>(3);
				actions.add(new SeasonDetailsAction());
				actions.add(null);
				actions.add(new NewSeasonAction(show));
				actions.add(new DeleteSeasonAction(frame));
				actions.add(null);
				actions.add(new SeasonEpisodesAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Season> getDoubleClickAction()
			{
				return new SeasonEpisodesAction(frame);
			}
		};
		return tableController.createComponent();
	}

	private void createTableData(SortableTableModel<Season> tableModel)
	{
		for (Season season : show.getSeasons()) tableModel.addRow(new SeasonTableRow(season));
		tableModel.sort();
		collectionObserver=new CollectionChangeObserver();
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
		show.removeCollectionListener(collectionObserver);
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.SEASONS.equals(event.getPropertyName()))
			{
				SortableTableModel<Season> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Season newSeason=(Season)event.getElement();
						SeasonTableRow row=new SeasonTableRow(newSeason);
						model.addRow(row);
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

	private static class SeasonTableRow extends SortableTableRow<Season> implements PropertyChangeListener
	{
		public SeasonTableRow(Season season)
		{
			super(season);
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
				Season season=getUserObject();
				return new Integer(season.getNumber());
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Season season=getUserObject();
			switch (column)
			{
				case 0:
					return season.getSeasonName();
				case 1:
					return season.getYearString();
			}
			return "";
		}
	}

	public static class SeasonEpisodesAction extends SimpleContextAction<Season>
	{
		private ApplicationFrame frame;

		public SeasonEpisodesAction(ApplicationFrame frame)
		{
			super("Episodes");
			this.frame=frame;
		}

		public void actionPerformed(ActionEvent e)
		{
			frame.setCurrentView(new EpisodesView(getObject()), true);
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), SeasonsView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new SeasonsView(show), true);
	}
}