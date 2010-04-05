package com.kiwisoft.media.movie;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.media.Name;
import com.kiwisoft.media.PinAction;
import com.kiwisoft.media.medium.CreateMediumAction;
import com.kiwisoft.media.person.ShowCreditsAction;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.SearchView;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieSearchView extends SearchView<Movie>
{
	public MovieSearchView()
	{
	}

	@Override
	public String getTitle()
	{
		return "Movies";
	}

	@Override
	protected TableController<Movie> createResultTable(final ApplicationFrame frame)
	{
		SortableTableModel<Movie> tableModel=new DefaultSortableTableModel<Movie>(Movie.TITLE, Movie.GERMAN_TITLE, Movie.YEAR);
		return new TableController<Movie>(tableModel, new DefaultTableConfiguration("movies.list", MovieSearchView.class, "movies"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MovieDetailsAction());
				actions.add(new NewMovieAction(null));
				actions.add(new DeleteMovieAction(frame, null));
				actions.add(new PinAction(MovieSearchView.this));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new MovieDetailsAction());
				actions.add(null);
				actions.add(new NewMovieAction(null));
				actions.add(new DeleteMovieAction(frame, null));
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
	}

	@Override
	protected Set<Movie> doSearch(String searchText)
	{
		if (StringUtils.isEmpty(searchText)) return DBLoader.getInstance().loadSet(Movie.class, null, "limit 1001");
		if (searchText.contains("*")) searchText=searchText.replace('*', '%');
		else searchText="%"+searchText+"%";
		Set<Movie> movies=new HashSet<Movie>();
		movies.addAll(DBLoader.getInstance().loadSet(Movie.class, null,
													 "title like ? or german_title like ? limit 1001",
													 searchText, searchText));
		if (movies.size()<1001)
		{
			movies.addAll(DBLoader.getInstance().loadSet(Movie.class, "names",
														 "names.type=? and names.ref_id=movies.id and names.name like ?", Name.MOVIE, searchText));
		}
		return movies;
	}

	@Override
	protected void installCollectionListener()
	{
		getModelListenerList().addDisposable(MovieManager.getInstance().addCollectionChangeListener(new CollectionObserver(MovieManager.MOVIES)));
		super.installCollectionListener();
	}
}