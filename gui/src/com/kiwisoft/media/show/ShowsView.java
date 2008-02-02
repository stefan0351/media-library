/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.show;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.media.*;
import com.kiwisoft.media.dataImport.TVTVDeLoaderContextAction;
import com.kiwisoft.media.dataImport.SerienJunkiesDeLoaderAction;
import com.kiwisoft.media.dataImport.ProSiebenDeLoaderAction;
import com.kiwisoft.media.dataImport.TVComLoaderAction;
import com.kiwisoft.swing.table.TableController;
import com.kiwisoft.collection.CollectionChangeEvent;
import com.kiwisoft.collection.CollectionChangeListener;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.actions.ComplexAction;
import com.kiwisoft.swing.table.SortableTableModel;
import com.kiwisoft.swing.table.SortableTableRow;
import com.kiwisoft.swing.table.DefaultSortableTableModel;
import com.kiwisoft.swing.table.DefaultTableConfiguration;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;

public class ShowsView extends ViewPanel
{
	private Genre genre;

	private ShowListener showListener;
	private TableController<Show> tableController;

	public ShowsView(Genre genre)
	{
		this.genre=genre;
	}

	protected JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Show> tmShows=new DefaultSortableTableModel<Show>("title", "germanTitle", "type");
		Collection<Show> shows;
		if (genre!=null) shows=genre.getShows();
		else shows=ShowManager.getInstance().getShows();
		for (Show show : shows) tmShows.addRow(new ShowTableRow(show));
		tmShows.sort();

		tableController=new TableController<Show>(tmShows, new DefaultTableConfiguration(ShowsView.class, "shows"))
		{
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new ShowDetailsAction());
				actions.add(new NewShowAction());
				actions.add(new DeleteShowAction(frame));
				actions.add(new ShowSeasonsAction(frame));
				actions.add(new ShowEpisodesAction(frame));
				return actions;
			}

			public List<ContextAction> getContextActions()
			{
				ComplexAction downloadAction=new ComplexAction("Download");
				downloadAction.addAction(new ProSiebenDeLoaderAction(frame));
				downloadAction.addAction(new TVTVDeLoaderContextAction(frame));
				downloadAction.addSeparator();
				downloadAction.addAction(new TVComLoaderAction(frame));
				downloadAction.addAction(new SerienJunkiesDeLoaderAction(frame));

				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new ShowDetailsAction());
				actions.add(null);
				actions.add(new NewShowAction());
				actions.add(new DeleteShowAction(frame));
				actions.add(null);
				actions.add(new ShowEpisodesAction(frame));
				actions.add(new ShowSeasonsAction(frame));
				actions.add(new ShowAirdatesAction(frame));
				actions.add(new ShowMoviesAction(frame));
				actions.add(new ShowCastAction(frame));
				actions.add(new ShowTracksAction(frame));
				actions.add(new ShowLinksAction(frame));
				actions.add(downloadAction);
				return actions;
			}

			public ContextAction getDoubleClickAction()
			{
				return new ShowEpisodesAction(frame);
			}
		};

		showListener=new ShowListener();
		ShowManager.getInstance().addCollectionChangeListener(showListener);

		return tableController.createComponent();
	}

	public String getTitle()
	{
		if (genre==null) return "Shows";
		else return "Shows - "+genre.getName();
	}

	protected void installComponentListeners()
	{
		tableController.installListeners();
	}

	protected void removeComponentListeners()
	{
		tableController.removeListeners();
	}

	public void dispose()
	{
		ShowManager.getInstance().removeCollectionListener(showListener);
		tableController.dispose();
	}

	private class ShowListener implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (ShowManager.SHOWS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Show newShow=(Show)event.getElement();
						ShowTableRow row=new ShowTableRow(newShow);
						tableController.getModel().addRow(row);
						break;
					case CollectionChangeEvent.REMOVED:
						SortableTableModel<Show> model=tableController.getModel();
						int index=model.indexOf(event.getElement());
						if (index>=0) model.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class ShowTableRow extends SortableTableRow<Show> implements PropertyChangeListener, CollectionChangeListener
	{
		public ShowTableRow(Show show)
		{
			super(show);
		}

		public void installListener()
		{
			try
			{
				getUserObject().addPropertyChangeListener(this);
				getUserObject().addCollectionListener(this);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
			getUserObject().removeCollectionListener(this);
		}

		public Object getDisplayValue(int column, String property)
		{
			switch (column)
			{
				case 0:
					return getUserObject().getTitle();
				case 1:
					return getUserObject().getGermanTitle();
				case 2:
					return StringUtils.formatAsEnumeration(getUserObject().getGenres(), ", ");
			}
			return "";
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public void collectionChanged(CollectionChangeEvent event)
		{
			fireRowUpdated();
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getTitle(), ShowsView.class);
		if (genre!=null) bookmark.setParameter("genre_id", genre.getId().toString());
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String genreId=bookmark.getParameter("genre_id");
		Genre genre=null;
		if (genreId!=null) genre=DBLoader.getInstance().load(Genre.class, new Long(genreId));
		frame.setCurrentView(new ShowsView(genre), true);
	}
}
