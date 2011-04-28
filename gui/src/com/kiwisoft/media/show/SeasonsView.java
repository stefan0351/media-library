package com.kiwisoft.media.show;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.CollectionPropertyChangeAdapter;
import com.kiwisoft.utils.CollectionPropertyChangeEvent;

public class SeasonsView extends ViewPanel
{
	// Dates Panel
	private Show show;
	private TableController<Season> tableController;

	public SeasonsView(Show show)
	{
		this.show=show;
	}

	@Override
	public String getTitle()
	{
		return show.getTitle()+" - Seasons";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Season> tableModel=new DefaultSortableTableModel<Season>("name", "years");
		createTableData(tableModel);

		tableController=new TableController<Season>(tableModel, new DefaultTableConfiguration("seasons.list", SeasonsView.class, "seasons"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(3);
				actions.add(new SeasonDetailsAction());
				actions.add(new NewSeasonAction(show));
				actions.add(new DeleteSeasonAction(frame));
				actions.add(new SeasonEpisodesAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>(3);
				actions.add(new SeasonDetailsAction());
				actions.add(null);
				actions.add(new NewSeasonAction(show));
				actions.add(new DeleteSeasonAction(frame));
				actions.add(null);
				actions.add(new SeasonEpisodesAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new SeasonEpisodesAction(frame);
			}
		};
		return tableController.getComponent();
	}

	private void createTableData(SortableTableModel<Season> tableModel)
	{
		for (Season season : show.getSeasons()) tableModel.addRow(new SeasonTableRow(season));
		tableModel.sort();
		getModelListenerList().installPropertyChangeListener(show, new CollectionChangeObserver());
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

	private class CollectionChangeObserver extends CollectionPropertyChangeAdapter
	{
		@Override
		public void collectionChange(CollectionPropertyChangeEvent event)
		{
			if (Show.SEASONS.equals(event.getPropertyName()))
			{
				SortableTableModel<Season> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionPropertyChangeEvent.ADDED:
						Season newSeason=(Season)event.getElement();
						SeasonTableRow row=new SeasonTableRow(newSeason);
						model.addRow(row);
						model.sort();
						break;
					case CollectionPropertyChangeEvent.REMOVED:
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

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		@Override
		public Comparable getSortValue(int column, String property)
		{
			if (column==0)
			{
				Season season=getUserObject();
				return new Integer(season.getNumber());
			}
			return super.getSortValue(column, property);
		}

		@Override
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

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), SeasonsView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new SeasonsView(show));
	}
}