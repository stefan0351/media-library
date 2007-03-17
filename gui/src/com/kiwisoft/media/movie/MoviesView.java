package com.kiwisoft.media.movie;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.video.CreateVideoAction;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class MoviesView extends ViewPanel
{
	// Dates Panel
	private Show show;
	private CollectionChangeObserver collectionObserver;
	private TableController<Movie> tableController;

	public MoviesView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		if (show!=null) return show.getTitle()+" - Movies";
		return "Movies";
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		MoviesTableModel tableModel=new MoviesTableModel();
		createTableData(tableModel);

		tableController=new TableController<Movie>(tableModel, new MediaTableConfiguration("table.movies"))
		{
			@Override
			public List<ContextAction<Movie>> getToolBarActions()
			{
				List<ContextAction<Movie>> actions=new ArrayList<ContextAction<Movie>>();
				actions.add(new MovieDetailsAction());
				actions.add(new NewMovieAction(show));
				actions.add(new DeleteMovieAction(frame, show));
				return actions;
			}

			@Override
			public List<ContextAction<Movie>> getContextActions()
			{
				List<ContextAction<Movie>> actions=new ArrayList<ContextAction<Movie>>();
				actions.add(new MovieDetailsAction());
				actions.add(null);
				actions.add(new NewMovieAction(show));
				actions.add(new DeleteMovieAction(frame, show));
				actions.add(null);
				actions.add(new CreateVideoAction());
				return actions;
			}

			@Override
			public ContextAction<Movie> getDoubleClickAction()
			{
				return new MovieDetailsAction();
			}
		};
		return tableController.createComponent();
	}

	private void createTableData(MoviesTableModel tableModel)
	{
		Iterator<Movie> it;
		if (show!=null) it=show.getMovies().iterator();
		else it=MovieManager.getInstance().getMovies().iterator();
		while (it.hasNext())
		{
			Movie movie=it.next();
			tableModel.addRow(new Row(movie));
		}
		tableModel.sort();
		collectionObserver=new CollectionChangeObserver();
		MovieManager.getInstance().addCollectionChangeListener(collectionObserver);
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
		MovieManager.getInstance().removeCollectionListener(collectionObserver);
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (MovieManager.MOVIES.equals(event.getPropertyName()))
			{
				SortableTableModel<Movie> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Movie newMovie=(Movie)event.getElement();
						if (show==null || newMovie.getShow()==show)
						{
							Row row=new Row(newMovie);
							model.addRow(row);
							model.sort();
						}
						break;
					case CollectionChangeEvent.REMOVED:
						int index=model.indexOf(event.getElement());
						if (index>=0) model.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class MoviesTableModel extends SortableTableModel<Movie>
	{
		private static final String[] COLUMNS={"title", "germanTitle", "year"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class Row extends SortableTableRow<Movie> implements PropertyChangeListener
	{
		public Row(Movie movie)
		{
			super(movie);
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

		public Object getDisplayValue(int column, String property)
		{
			Movie movie=getUserObject();
			switch (column)
			{
				case 0:
					return movie.getTitle();
				case 1:
					return movie.getGermanTitle();
				case 2:
					return movie.getYear();
			}
			return null;
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), MoviesView.class);
		if (show!=null) bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String showId=bookmark.getParameter("show");
		Show show=null;
		if (showId!=null) show=ShowManager.getInstance().getShow(new Long(showId));
		frame.setCurrentView(new MoviesView(show), true);
	}
}