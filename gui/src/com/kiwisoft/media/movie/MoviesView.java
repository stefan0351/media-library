package com.kiwisoft.media.movie;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.kiwisoft.media.Name;
import com.kiwisoft.media.video.CreateVideoAction;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBLoader;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.DefaultSortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;

public class MoviesView extends ViewPanel
{
	// Dates Panel
	private Show show;
	private CollectionChangeObserver collectionObserver;
	private TableController<Movie> tableController;
	private JLabel resultLabel;

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
		SortableTableModel<Movie> tableModel=new DefaultSortableTableModel<Movie>("title", "germanTitle", "year");
		collectionObserver=new CollectionChangeObserver();
		MovieManager.getInstance().addCollectionChangeListener(collectionObserver);

		tableController=new TableController<Movie>(tableModel, new DefaultTableConfiguration(MoviesView.class, "movies"))
		{
			@Override
			public List<ContextAction<? super Movie>> getToolBarActions()
			{
				List<ContextAction<? super Movie>> actions=new ArrayList<ContextAction<? super Movie>>();
				actions.add(new MovieDetailsAction());
				actions.add(new NewMovieAction(show));
				actions.add(new DeleteMovieAction(frame, show));
				return actions;
			}

			@Override
			public List<ContextAction<? super Movie>> getContextActions()
			{
				List<ContextAction<? super Movie>> actions=new ArrayList<ContextAction<? super Movie>>();
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

		JTextField searchField=new JTextField();
		searchField.addActionListener(new SearchActionListener(searchField));

		resultLabel=new JLabel("No search executed.");

		JPanel panel=new JPanel(new BorderLayout(0, 10));
		panel.add(searchField, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		panel.add(resultLabel, BorderLayout.SOUTH);

		return panel;
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
			if ("title".equals(property)) return getUserObject().getTitle();
			else if ("germanTitle".equals(property)) return getUserObject().getGermanTitle();
			else if ("year".equals(property)) return getUserObject().getYear();
			else if ("poster".equals(property)) return getUserObject().hasPoster();
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

	private class SearchActionListener implements ActionListener
	{
		private final JTextField searchField;

		public SearchActionListener(JTextField searchField)
		{
			this.searchField=searchField;
		}

		public void actionPerformed(ActionEvent e)
		{
			String searchText=searchField.getText();

			Set<Movie> movies;
			if (StringUtils.isEmpty(searchText)) movies=MovieManager.getInstance().getMovies();
			else
			{
				if (searchText.contains("*")) searchText=searchText.replace('*', '%');
				else searchText="%"+searchText+"%";
				movies=new HashSet<Movie>();
				movies.addAll(DBLoader.getInstance().loadSet(Movie.class, null,
															 "title like ? or german_title like ? limit 1001",
															 searchText, searchText));
				movies.addAll(DBLoader.getInstance().loadSet(Movie.class, "names",
															 "names.type=? and names.ref_id=movies.id and names.name like ?", Name.MOVIE, searchText));
			}
			SortableTableModel<Movie> tableModel=tableController.getModel();
			tableModel.clear();
			List<Row> rows=new ArrayList<Row>(movies.size());
			for (Movie movie : movies) rows.add(new Row(movie));
			tableModel.addRows(rows);
			tableModel.sort();
			int rowCount=rows.size();
			if (rows.isEmpty()) resultLabel.setText("No rows found.");
			else if (rowCount==1) resultLabel.setText("1 row found.");
			else if (rowCount>1000) resultLabel.setText("More than 1000 Row(s) found.");
			else resultLabel.setText(rowCount+" rows found.");
		}
	}
}