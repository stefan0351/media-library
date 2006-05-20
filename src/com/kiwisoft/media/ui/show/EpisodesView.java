package com.kiwisoft.media.ui.show;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.Season;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.ui.MediaManagerFrame;
import com.kiwisoft.media.ui.video.VideoDetailsView;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.CollectionChangeEvent;
import com.kiwisoft.utils.CollectionChangeListener;
import com.kiwisoft.utils.Configurator;
import com.kiwisoft.utils.db.*;
import com.kiwisoft.utils.gui.table.DynamicTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableConfiguration;

public class EpisodesView extends ViewPanel
{
	private Show show;
	private Season season;

	// Dates Panel
	private DynamicTable tblEpisodes;
	private EpisodesTableModel tmEpisodes;
	private DoubleClickListener doubleClickListener;
	private CollectionChangeObserver collectionObserver;
	private JScrollPane scrlEpisodes;

	public EpisodesView(Show show)
	{
		this.show=show;
	}

	public EpisodesView(Season season)
	{
		this.season=season;
		this.show=season.getShow();
	}

	public String getName()
	{
		if (season!=null)
			return show.getName()+" - "+season.getSeasonName()+" - Episoden";
		else
			return show.getName()+" - Episoden";
	}

	public JComponent createContentPanel()
	{
		tmEpisodes=new EpisodesTableModel();
		createTableData();

		tblEpisodes=new DynamicTable(tmEpisodes);
		tblEpisodes.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblEpisodes.initializeColumns(new TableConfiguration(Configurator.getInstance(), MediaManagerFrame.class, "table.episodes"));
		tblEpisodes.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK), "new episode");
		tblEpisodes.getActionMap().put("new episode", new NewEpisodeAction());

		scrlEpisodes=new JScrollPane(tblEpisodes);
		return scrlEpisodes;
	}

	private void createTableData()
	{
		collectionObserver=new CollectionChangeObserver();
		Iterator it;
		if (season!=null)
			it=season.getEpisodes().iterator();
		else
		{
			Chain episodes=show.getEpisodes();
			it=episodes.iterator();
			episodes.addChainListener(collectionObserver);
		}
		while (it.hasNext())
		{
			Episode episode=(Episode)it.next();
			tmEpisodes.addRow(new EpisodeTableRow(episode));
		}
		tmEpisodes.sort();
		show.addCollectionChangeListener(collectionObserver);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblEpisodes.addMouseListener(doubleClickListener);
		scrlEpisodes.addMouseListener(doubleClickListener);
		super.installComponentListener();
	}

	protected void removeComponentListeners()
	{
		tblEpisodes.removeMouseListener(doubleClickListener);
		scrlEpisodes.removeMouseListener(doubleClickListener);
		super.removeComponentListeners();
	}

	public void dispose()
	{
		if (season==null) show.getEpisodes().removeChainListener(collectionObserver);
		show.removeCollectionListener(collectionObserver);
		tmEpisodes.clear();
		super.dispose();
	}

	private class CollectionChangeObserver implements CollectionChangeListener, ChainListener
	{
		public void collectionChanged(CollectionChangeEvent event)
		{
			if (Show.EPISODES.equals(event.getPropertyName()))
			{
				switch (event.getType())
				{
					case CollectionChangeEvent.ADDED:
						Episode newEpisode=(Episode)event.getElement();
						EpisodeTableRow row=new EpisodeTableRow(newEpisode);
						int newIndex=tmEpisodes.addRow(row);
						tmEpisodes.sort();
						tblEpisodes.getSelectionModel().setSelectionInterval(newIndex, newIndex);
						tblEpisodes.scrollRectToVisible(tblEpisodes.getCellRect(newIndex, 0, false));
						break;
					case CollectionChangeEvent.REMOVED:
						int index=tmEpisodes.indexOf(event.getElement());
						if (index>=0) tmEpisodes.removeRowAt(index);
						break;
				}
			}
		}

		public void chainChanged(ChainEvent event)
		{
			switch (event.getType())
			{
				case ChainEvent.CHANGED:
					tmEpisodes.sort();
			}
		}
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblEpisodes.rowAtPoint(e.getPoint());
				if (rowIndex>=0)
				{
					SortableTableRow row=tmEpisodes.getRow(rowIndex);
					if (row!=null) EpisodeDetailsView.create((Episode)row.getUserObject());
				}
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblEpisodes.getSelectedRows();
				Set episodes=new LinkedHashSet();
				for (int i=0; i<rows.length; i++) episodes.add(tmEpisodes.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new CreateSeasonAction(episodes));
				popupMenu.add(new CreateVideoAction(episodes));
				popupMenu.addSeparator();
				popupMenu.add(new NewEpisodeAction());
				popupMenu.add(new DeleteEpisodeAction(episodes));
				popupMenu.addSeparator();
				popupMenu.add(new MoveUpAction(episodes));
				popupMenu.show(tblEpisodes, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private static class EpisodesTableModel extends SortableTableModel
	{
		private static final String[] COLUMNS={"userkey", "title", "originalTitel"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private class EpisodeTableRow extends SortableTableRow implements PropertyChangeListener
	{
		public EpisodeTableRow(Episode episode)
		{
			super(episode);
		}

		public void installListener()
		{
			((Episode)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Episode)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireRowUpdated();
		}

		public Comparable getSortValue(int column, String property)
		{
			if (column==0)
			{
				Episode episode=(Episode)getUserObject();
				return new Integer(episode.getChainPosition());
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Episode episode=(Episode)getUserObject();
			switch (column)
			{
				case 0:
					return episode.getUserKey();
				case 1:
					return episode.getName();
				case 2:
					return episode.getOriginalName();
			}
			return "";
		}
	}

	private class NewEpisodeAction extends AbstractAction
	{
		public NewEpisodeAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			EpisodeDetailsView.create(show);
		}
	}

	private class CreateSeasonAction extends AbstractAction
	{
		private Episode firstEpisode;
		private Episode lastEpisode;

		public CreateSeasonAction(Collection episodes)
		{
			super("Erzeuge Staffel");
			if (!episodes.isEmpty())
			{
				TreeSet sortedSet=new TreeSet(episodes);
				firstEpisode=(Episode)sortedSet.first();
				lastEpisode=(Episode)sortedSet.last();
				setEnabled(true);
			}
			else
				setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			SeasonDetailsView.create(firstEpisode, lastEpisode);
		}
	}

	private class CreateVideoAction extends AbstractAction
	{
		private Set episodes;

		public CreateVideoAction(Set episodes)
		{
			super("Erzeuge Video");
			this.episodes=episodes;
			setEnabled(!episodes.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			VideoDetailsView.create(episodes);
		}
	}

	private class DeleteEpisodeAction extends AbstractAction
	{
		private Collection episodes;

		public DeleteEpisodeAction(Collection episodes)
		{
			super("Löschen");
			this.episodes=episodes;
			setEnabled(!episodes.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			Iterator it=episodes.iterator();
			while (it.hasNext())
			{
				Episode episode=(Episode)it.next();
				if (episode.isUsed())
				{
					JOptionPane.showMessageDialog(EpisodesView.this,
							"Die Folge '"+episode.getName()+"' kann nicht gelöscht werden.",
							"Meldung",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(EpisodesView.this,
					"Episoden wirklick löschen?",
					"Löschen?",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					it=episodes.iterator();
					while (it.hasNext())
					{
						Episode episode=(Episode)it.next();
						show.dropEpisode(episode);
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
					JOptionPane.showMessageDialog(EpisodesView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class MoveUpAction extends AbstractAction
	{
		private Set episodes;

		public MoveUpAction(Set episodes)
		{
			super("Nach oben");
			this.episodes=new TreeSet(Chain.getComparator());
			this.episodes.addAll(episodes);
			setEnabled(season==null && !this.episodes.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			tblEpisodes.clearSelection();

			Transaction transaction=null;
			try
			{
				transaction=DBSession.getInstance().createTransaction();
				Iterator it=episodes.iterator();
				while (it.hasNext()) show.getEpisodes().moveUp((Episode)it.next());
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
				JOptionPane.showMessageDialog(EpisodesView.this, e1.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
			}

			Iterator it=episodes.iterator();
			while (it.hasNext())
			{
				Object o=it.next();
				int rowIndex=tmEpisodes.indexOf(o);
				if (rowIndex>=0) tblEpisodes.getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
			}
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), EpisodesView.class);
		if (season!=null)
		{
			bookmark.setParameter("type", "season");
			bookmark.setParameter("id", String.valueOf(season.getId()));
		}
		else
		{
			bookmark.setParameter("type", "show");
			bookmark.setParameter("id", String.valueOf(show.getId()));
		}
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String type=bookmark.getParameter("type");
		if ("season".equals(type))
		{
			Long id=new Long(bookmark.getParameter("id"));
			Season season=ShowManager.getInstance().getSeason(id);
			frame.setCurrentView(new EpisodesView(season), true);
		}
		else
		{
			Long id=new Long(bookmark.getParameter("id"));
			Show show=ShowManager.getInstance().getShow(id);
			frame.setCurrentView(new EpisodesView(show), true);
		}
	}
}