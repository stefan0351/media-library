/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Apr 1, 2003
 * Time: 7:42:37 PM
 */
package com.kiwisoft.media.ui.show;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.ui.*;
import com.kiwisoft.media.ui.movie.MoviesView;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;
import com.kiwisoft.utils.gui.ApplicationFrame;

public class ShowsView extends ViewPanel
{
	private DynamicTable tblShows;
	private DoubleClickListener doubleClickListener;
	private ShowsTableModel tmShows;
	private ShowListener showListener;

	protected JComponent createContentPanel()
	{
		tmShows=new ShowsTableModel();
		Iterator it=ShowManager.getInstance().getShows().iterator();
		while (it.hasNext())
		{
			Show show=(Show)it.next();
			tmShows.addRow(new ShowTableRow(show));
		}
		tmShows.sort();

		tblShows=new DynamicTable(tmShows);
		tblShows.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblShows.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.shows"));
		showListener=new ShowListener();
		ShowManager.getInstance().addCollectionChangeListener(showListener);

		return new JScrollPane(tblShows);
	}

	public String getName()
	{
		return "Serien";
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblShows.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		tblShows.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		ShowManager.getInstance().removeCollectionListener(showListener);
		tmShows.clear();
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblShows.rowAtPoint(e.getPoint());
				SortableTableRow row=tmShows.getRow(rowIndex);
				if (row!=null)
				{
					MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
					wizard.setCurrentView(new EpisodesView((Show)row.getUserObject()), true);
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblShows.getSelectedRows();
				Set<Show> shows=new HashSet<Show>();
				for (int row : rows) shows.add(tmShows.getRow(row).getUserObject());
				Show show=null;
				if (rows.length==1) show=(Show)tmShows.getObject(rows[0]);

				MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();

				JMenu menuDownload=new JMenu("Download");
				menuDownload.add(new DownloadP7Action(wizard, shows));
				menuDownload.add(new DownloadTVTVAction(wizard, shows));

				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new ShowEpisodesAction(show));
				popupMenu.add(new ShowSeasonsAction(show));
				popupMenu.add(new ShowAirdatesAction(show));
				popupMenu.add(new ShowPropertiesAction(show));
				popupMenu.add(new ShowMoviesAction(shows));
				popupMenu.add(new ShowCastAction(show));
				popupMenu.add(new RecordingsAction(show));
				popupMenu.add(new ShowLinksAction(show));
				popupMenu.add(menuDownload);
				popupMenu.addSeparator();
				popupMenu.add(new NewShowAction());
				popupMenu.add(new DeleteShowAction(show));
				popupMenu.show(tblShows, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
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
						tmShows.addRow(row);
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmShows.indexOf(event.getElement());
						if (index>=0) tmShows.removeRowAt(index);
						break;
				}
			}
		}
	}

	private static class ShowsTableModel extends SortableTableModel<ShowTableRow>
	{
		private static final String[] COLUMNS={"name", "originalName", "type"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
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
			getUserObject().addPropertyChangeListener(this);
			getUserObject().addCollectionChangeListener(this);
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
					return getUserObject().getName();
				case 1:
					return getUserObject().getOriginalName();
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

	private static class NewShowAction extends AbstractAction
	{
		public NewShowAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			ShowDetailsView.create(null);
		}
	}

	private class ShowAirdatesAction extends AbstractAction
	{
		private Show show;

		public ShowAirdatesAction(Show show)
		{
			super("Sendetermine");
			this.show=show;
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new AirdatesView(show), true);
		}
	}

	private class ShowCastAction extends AbstractAction
	{
		private Show show;

		public ShowCastAction(Show show)
		{
			super("Darsteller");
			this.show=show;
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new ShowCastView(show), true);
		}
	}

	private class ShowSeasonsAction extends AbstractAction
	{
		private Show show;

		public ShowSeasonsAction(Show show)
		{
			super("Staffeln");
			this.show=show;
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new SeasonsView(show), true);
		}
	}

	private class ShowEpisodesAction extends AbstractAction
	{
		private Show show;

		public ShowEpisodesAction(Show show)
		{
			super("Episoden");
			this.show=show;
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new EpisodesView(show), true);
		}
	}

	private class ShowLinksAction extends AbstractAction
	{
		private Show show;

		public ShowLinksAction(Show show)
		{
			super("Links");
			this.show=show;
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new LinksView(show), true);
		}
	}

	private class RecordingsAction extends AbstractAction
	{
		private Show show;

		public RecordingsAction(Show show)
		{
			super("Aufnahmen");
			this.show=show;
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new ShowRecordingsView(show), true);
		}
	}

	private class ShowMoviesAction extends AbstractAction
	{
		private Show show;

		public ShowMoviesAction(Set shows)
		{
			super("Filme");
			if (shows.size()==1) show=(Show)shows.iterator().next();
			setEnabled(show!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)ShowsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new MoviesView(show), true);
		}
	}

	private static class ShowPropertiesAction extends AbstractAction
	{
		private Show show;

		public ShowPropertiesAction(Show show)
		{
			super("Eigenschaften");
			this.show=show;
			if (show==null) setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			ShowDetailsView.create(show);
		}
	}

	private class DeleteShowAction extends AbstractAction
	{
		private Show show;

		public DeleteShowAction(Show show)
		{
			super("Löschen");
			this.show=show;
		}

		public void actionPerformed(ActionEvent event)
		{
			if (show.isUsed())
			{
				JOptionPane.showMessageDialog(ShowsView.this,
				        "Die Serie '"+show.getName()+"' kann nicht gelöscht werden.",
				        "Meldung",
				        JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option=JOptionPane.showConfirmDialog(ShowsView.this,
			        "Serie '"+show.getName()+"' wirklick löschen?",
			        "Löschen?",
			        JOptionPane.YES_NO_OPTION,
			        JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					ShowManager.getInstance().dropShow(show);
					transaction.close();
				}
				catch (Exception e)
				{
					if (transaction!=null)
					{
						try
						{
							transaction.rollback();
						}
						catch (SQLException e1)
						{
							e1.printStackTrace();
							JOptionPane.showMessageDialog(ShowsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(ShowsView.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		return new Bookmark(getName(), ShowsView.class);
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		assert bookmark!=null;
		frame.setCurrentView(new ShowsView(), true);
	}
}
