package com.kiwisoft.media.movie;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.media.medium.CreateMediumAction;
import com.kiwisoft.media.person.ShowCreditsAction;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShowMoviesView extends ViewPanel
{
	// Dates Panel
	private Show show;
	private CollectionChangeObserver collectionObserver;
	private TableController<Movie> tableController;

	public ShowMoviesView(Show show)
	{
		this.show=show;
	}

	@Override
	public String getTitle()
	{
		if (show!=null) return show.getTitle()+" - Movies";
		return "Movies";
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Movie> tableModel=new DefaultSortableTableModel<Movie>(Movie.TITLE, Movie.GERMAN_TITLE, Movie.YEAR);
		collectionObserver=new CollectionChangeObserver();
		MovieManager.getInstance().addCollectionChangeListener(collectionObserver);

		tableController=new TableController<Movie>(tableModel, new DefaultTableConfiguration("movies.list", MovieSearchView.class, "movies"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MovieDetailsAction());
				actions.add(new NewMovieAction(show));
				actions.add(new DeleteMovieAction(frame, show));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MovieDetailsAction());
				actions.add(null);
				actions.add(new NewMovieAction(show));
				actions.add(new DeleteMovieAction(frame, show));
				actions.add(null);
				actions.add(new CreateMediumAction());
				actions.add(null);
				actions.add(new ShowCreditsAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new MovieDetailsAction();
			}
		};

		return tableController.getComponent();
	}

	@Override
	protected void initializeData()
	{
		Set<Movie> movies=show.getMovies();
		SortableTableModel<Movie> tableModel=tableController.getModel();
		tableModel.clear();
		for (Movie movie : movies) tableModel.addRow(new BeanTableRow<Movie>(movie));
		tableModel.sort();
		super.initializeData();
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
		MovieManager.getInstance().removeCollectionListener(collectionObserver);
		tableController.dispose();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		@Override
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (MovieManager.MOVIES.equals(event.getPropertyName()))
			{
				SortableTableModel<Movie> model=tableController.getModel();
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Movie newMovie=(Movie) event.getElement();
						if (newMovie.getShow()==show)
						{
							BeanTableRow<Movie> row=new BeanTableRow<Movie>(newMovie);
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

	@Override
	public boolean isBookmarkable()
	{
		return true;
	}

	@Override
	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), ShowMoviesView.class);
		if (show!=null) bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String showId=bookmark.getParameter("show");
		Show show=ShowManager.getInstance().getShow(new Long(showId));
		if (show!=null)
		{
			frame.setCurrentView(new ShowMoviesView(show));
		}
	}
}