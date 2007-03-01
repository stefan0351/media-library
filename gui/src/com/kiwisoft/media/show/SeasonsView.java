package com.kiwisoft.media.show;

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

import com.kiwisoft.media.MediaManagerFrame;
import com.kiwisoft.media.MediaTableConfiguration;
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

public class SeasonsView extends ViewPanel
{
	// Dates Panel
	private SortableTable tblSeasons;
	private SeasonsTableModel tmSeasons;
	private DoubleClickListener doubleClickListener;
	private Show show;
	private CollectionChangeObserver collectionObserver;
	private JScrollPane scrlSeasons;

	public SeasonsView(Show show)
	{
		this.show=show;
	}

	public String getName()
	{
		return show.getName()+" - Staffeln";
	}

	public JComponent createContentPanel()
	{
		tmSeasons=new SeasonsTableModel();
		createTableData();

		tblSeasons=new SortableTable(tmSeasons);
		tblSeasons.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblSeasons.initializeColumns(new MediaTableConfiguration("table.seasons"));

		scrlSeasons=new JScrollPane(tblSeasons);
		return scrlSeasons;
	}

	private void createTableData()
	{
		Iterator it=show.getSeasons().iterator();
		while (it.hasNext())
		{
			Season season=(Season)it.next();
			tmSeasons.addRow(new SeasonTableRow(season));
		}
		tmSeasons.sort();
		collectionObserver=new CollectionChangeObserver();
		show.addCollectionChangeListener(collectionObserver);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblSeasons.addMouseListener(doubleClickListener);
		scrlSeasons.addMouseListener(doubleClickListener);
		super.installComponentListener();
	}

	protected void removeComponentListeners()
	{
		tblSeasons.removeMouseListener(doubleClickListener);
		scrlSeasons.removeMouseListener(doubleClickListener);
		super.removeComponentListeners();
	}

	public void dispose()
	{
		show.removeCollectionListener(collectionObserver);
		tmSeasons.clear();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.SEASONS.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Season newSeason=(Season)event.getElement();
						SeasonTableRow row=new SeasonTableRow(newSeason);
						tmSeasons.addRow(row);
						tmSeasons.sort();
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmSeasons.indexOf(event.getElement());
						if (index>=0) tmSeasons.removeRowAt(index);
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
				int rowIndex=tblSeasons.rowAtPoint(e.getPoint());
				if (rowIndex>=0)
				{
					SortableTableRow row=tmSeasons.getRow(rowIndex);
					if (row!=null) SeasonDetailsView.create((Season)row.getUserObject());
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblSeasons.getSelectedRows();
				Set seasons=new HashSet();
				for (int i=0; i<rows.length; i++) seasons.add(tmSeasons.getObject(rows[i]));
				Season season=null;
				if (seasons.size()==1) season=(Season)seasons.iterator().next();
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new ShowEpisodesAction(season));
				popupMenu.addSeparator();
				popupMenu.add(new NewSeasonAction());
				popupMenu.add(new DeleteSeasonAction(seasons));
				popupMenu.show(tblSeasons, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private static class SeasonsTableModel extends SortableTableModel
	{
		private static final String[] COLUMNS={"name", "years"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class SeasonTableRow extends SortableTableRow implements PropertyChangeListener
	{
		public SeasonTableRow(Season season)
		{
			super(season);
		}

		public void installListener()
		{
			((Season)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Season)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Comparable getSortValue(int column, String property)
		{
			if (column==0)
			{
				Season season=(Season)getUserObject();
				return new Integer(season.getNumber());
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Season season=(Season)getUserObject();
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

	private class ShowEpisodesAction extends AbstractAction
	{
		private Season season;

		public ShowEpisodesAction(Season season)
		{
			super("Episoden");
			this.season=season;
			setEnabled(season!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			MediaManagerFrame wizard=(MediaManagerFrame)SeasonsView.this.getTopLevelAncestor();
			wizard.setCurrentView(new EpisodesView(season), true);
		}
	}

	private class NewSeasonAction extends AbstractAction
	{
		public NewSeasonAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			SeasonDetailsView.create(show);
		}
	}

	private class DeleteSeasonAction extends AbstractAction
	{
		private Collection seasons;

		public DeleteSeasonAction(Collection seasons)
		{
			super("Löschen");
			this.seasons=seasons;
			setEnabled(!seasons.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			Iterator it=seasons.iterator();
			while (it.hasNext())
			{
				Season season=(Season)it.next();
				if (season.isUsed())
				{
					JOptionPane.showMessageDialog(SeasonsView.this,
												  "Die Staffel '"+season.getSeasonName()+"' kann nicht gelöscht werden.",
												  "Meldung",
												  JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(SeasonsView.this,
													 "Staffeln wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					it=seasons.iterator();
					while (it.hasNext())
					{
						Season season=(Season)it.next();
						show.dropSeason(season);
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
					JOptionPane.showMessageDialog(SeasonsView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
		Bookmark bookmark=new Bookmark(getName(), SeasonsView.class);
		bookmark.setParameter("show", String.valueOf(show.getId()));
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		Show show=ShowManager.getInstance().getShow(new Long(bookmark.getParameter("show")));
		frame.setCurrentView(new SeasonsView(show), true);
	}
}