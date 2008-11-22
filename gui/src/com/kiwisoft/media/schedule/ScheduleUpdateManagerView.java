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

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<SearchPattern> model=new DefaultSortableTableModel<SearchPattern>("type", "object", "pattern");

		tableController=new TableController<SearchPattern>(model, new DefaultTableConfiguration(ScheduleUpdateManagerView.class, "patterns"))
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
			public void instanceCreated(Object instance)
			{
				if (instance instanceof SearchPattern && tableController.getModel().indexOf(instance)<0)
				{
					tableController.getModel().addRow(new TableRow((SearchPattern)instance));
				}
			}

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
		tableController.dispose();
		super.dispose();
	}

	private static class TableRow extends SortableTableRow<SearchPattern> implements PropertyChangeListener
	{
		public TableRow(SearchPattern pattern)
		{
			super(pattern);
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
			if (getUserObject().getState()==IDObject.State.DELETED) fireRowDeleted();
			fireRowUpdated();
		}

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

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		return new Bookmark(getTitle(), ScheduleUpdateManagerView.class);
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		frame.setCurrentView(new ScheduleUpdateManagerView());
	}
}
