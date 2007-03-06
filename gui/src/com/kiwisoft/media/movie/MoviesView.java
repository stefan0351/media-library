package com.kiwisoft.media.movie;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.media.MediaTableConfiguration;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class MoviesView extends ViewPanel
{
	// Dates Panel
	private SortableTable tblMovies;
	private MoviesTableModel tmMovies;
	private DoubleClickListener doubleClickListener;
	private Show show;
	private CollectionChangeObserver collectionObserver;
	private JScrollPane scrlMovies;

	public MoviesView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		if (show!=null) return show.getName()+" - Filme";
		return "Filme";
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		tmMovies=new MoviesTableModel();
		createTableData();

		tblMovies=new SortableTable(tmMovies);
		tblMovies.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblMovies.initializeColumns(new MediaTableConfiguration("table.movies"));

		scrlMovies=new JScrollPane(tblMovies);
		return scrlMovies;
	}

	private void createTableData()
	{
		Iterator<Movie> it;
		if (show!=null) it=show.getMovies().iterator();
		else it=MovieManager.getInstance().getMovies().iterator();
		while (it.hasNext())
		{
			Movie movie=it.next();
			tmMovies.addRow(new Row(movie));
		}
		tmMovies.sort();
		collectionObserver=new CollectionChangeObserver();
		MovieManager.getInstance().addCollectionChangeListener(collectionObserver);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblMovies.addMouseListener(doubleClickListener);
		scrlMovies.addMouseListener(doubleClickListener);
		super.installComponentListener();
	}

	protected void removeComponentListeners()
	{
		tblMovies.removeMouseListener(doubleClickListener);
		scrlMovies.removeMouseListener(doubleClickListener);
		super.removeComponentListeners();
	}

	public void dispose()
	{
		MovieManager.getInstance().removeCollectionListener(collectionObserver);
		tmMovies.clear();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (MovieManager.MOVIES.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Movie newMovie=(Movie)event.getElement();
						if (show==null || newMovie.getShow()==show)
						{
							Row row=new Row(newMovie);
							tmMovies.addRow(row);
							tmMovies.sort();
						}
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmMovies.indexOf(event.getElement());
						if (index>=0) tmMovies.removeRowAt(index);
						break;
				}
			}
		}
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblMovies.rowAtPoint(e.getPoint());
				if (rowIndex>=0)
				{
					SortableTableRow row=tmMovies.getRow(rowIndex);
					if (row!=null) MovieDetailsView.create((Movie)row.getUserObject());
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblMovies.getSelectedRows();
				Set<Movie> movies=new HashSet<Movie>();
				for (int row : rows) movies.add(tmMovies.getObject(row));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new NewAction());
				popupMenu.add(new DeleteAction(movies));
				popupMenu.show(tblMovies, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private static class MoviesTableModel extends SortableTableModel<Movie>
	{
		private static final String[] COLUMNS={"title"};

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
			}
			return null;
		}
	}

	private class NewAction extends AbstractAction
	{
		public NewAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			MovieDetailsView.create(show);
		}
	}

	private class DeleteAction extends AbstractAction
	{
		private Collection<Movie> movies;

		public DeleteAction(Collection<Movie> movies)
		{
			super("Löschen");
			this.movies=movies;
			setEnabled(!movies.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			for (Movie movie : movies)
			{
				if (movie.isUsed())
				{
					JOptionPane.showMessageDialog(MoviesView.this,
							"Dr Film '"+movie.getTitle()+"' kann nicht gelöscht werden.",
							"Meldung",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(MoviesView.this,
													 "Filme wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					for (Movie movie : movies)
					{
						if (show!=null)
							show.dropMovie(movie);
						else
							MovieManager.getInstance().dropMovie(movie);
					}
					transaction.close();
				}
				catch (Exception e1)
				{
					try
					{
						if (transaction!=null) transaction.rollback();
					}
					catch (SQLException e2)
					{
						e2.printStackTrace();
					}
					e1.printStackTrace();
					JOptionPane.showMessageDialog(MoviesView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
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