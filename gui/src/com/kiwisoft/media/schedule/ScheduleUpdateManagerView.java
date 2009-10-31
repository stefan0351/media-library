package com.kiwisoft.media.schedule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.media.dataimport.SearchPattern;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.ClassListener;

public class ScheduleUpdateManagerView extends ViewPanel
{
	private TableController<SearchPattern> tableController;

	public ScheduleUpdateManagerView()
	{
		setTitle("TV Schedule Update Manager");
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<SearchPattern> model=new DefaultSortableTableModel<SearchPattern>("type", "object", "pattern");

		tableController=new TableController<SearchPattern>(model, new DefaultTableConfiguration("schedule.patterns", ScheduleUpdateManagerView.class, "patterns"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new SearchPatternDetailsAction());
				actions.add(new CreateSearchPatternAction());
				actions.add(new DeleteSearchPatternAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new SearchPatternDetailsAction());
				actions.add(null);
				actions.add(new CreateSearchPatternAction());
				actions.add(new DeleteSearchPatternAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new SearchPatternDetailsAction();
			}
		};

		getModelListenerList().installClassListener(SearchPattern.class, new ClassListener()
		{
			@Override
			public void instanceCreated(Object instance)
			{
				if (instance instanceof SearchPattern && tableController.getModel().indexOf(instance)<0)
				{
					tableController.getModel().addRow(new TableRow((SearchPattern)instance));
				}
			}

			@Override
			public void instanceChanged(PropertyChangeEvent event)
			{
			}
		});
		return tableController.createComponent();
	}


	@Override
	protected void initializeData()
	{
		super.initializeData();
		SortableTableModel<SearchPattern> tableModel=tableController.getModel();
		tableModel.clear();

		Set<SearchPattern> patterns=DBLoader.getInstance().loadSet(SearchPattern.class, null, "type=?", SearchPattern.TVTV);
		for (SearchPattern pattern : patterns) tableModel.addRow(new TableRow(pattern));
		tableModel.sort();
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

	private static class TableRow extends SortableTableRow<SearchPattern> implements PropertyChangeListener
	{
		public TableRow(SearchPattern pattern)
		{
			super(pattern);
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
			if (getUserObject().getState()==IDObject.State.DELETED) fireRowDeleted();
			fireRowUpdated();
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			IDObject object=getUserObject().getReference();
			if ("type".equals(property))
			{
				if (object instanceof Show) return "Show";
				if (object instanceof Movie) return "Movie";
				if (object instanceof Person) return "Person";
			}
			else if ("object".equals(property)) return object;
			else if ("pattern".equals(property)) return getUserObject().getPattern();
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
		return new Bookmark(getTitle(), ScheduleUpdateManagerView.class);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ScheduleUpdateManagerView());
	}
}
