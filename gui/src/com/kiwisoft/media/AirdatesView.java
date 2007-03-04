package com.kiwisoft.media;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.media.show.EpisodeDetailsView;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.show.EpisodeUpdater;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.ClassObserver;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.DBObject;
import com.kiwisoft.utils.db.DBSession;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.db.Transaction;
import com.kiwisoft.utils.filter.ObjectFilter;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.progress.ProgressDialog;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;

public class AirdatesView extends ViewPanel
{
	private Show show;

	// Dates Panel
	private SortableTable tblDates;
	private AirdatesTableModel tmDates;
	private String title;
	private Collection airdates;
	private ObjectFilter filter;
	private DoubleClickListener doubleClickListener;
	private AirdatesListener airdatesListener;
	private int unit;
	private int quantity;

	public AirdatesView(Show show)
	{
		this.show=show;
		this.title="Sendetermine für "+show.getName();
		this.airdates=show.getAirdates();
		this.filter=new ShowFilter(show);
	}

	public AirdatesView(int unit, int quantity)
	{
		this.unit=unit;
		this.quantity=quantity;
		this.airdates=AirdateManager.getInstance().getAirdates(unit, quantity);
		this.title="Aktuelle Sendetermine";
	}

	public String getName()
	{
		return title;
	}

	public JComponent createContentPanel(ApplicationFrame frame)
	{
		tmDates=new AirdatesTableModel();
		Iterator it=airdates.iterator();
		while (it.hasNext())
		{
			Airdate date=(Airdate)it.next();
			tmDates.addRow(new AirdatesTableRow(date));
		}
		tmDates.sort();
		airdates=null;

		if (filter!=null)
		{
			airdatesListener=new AirdatesListener();
			DBObject.addClassObserver(airdatesListener, Airdate.class);
		}

		tblDates=new SortableTable(tmDates);
		tblDates.setPreferredScrollableViewportSize(new Dimension(200, 200));
		tblDates.initializeColumns(new MediaTableConfiguration("table.airdates"));

		return new JScrollPane(tblDates);
	}

	protected void installComponentListener()
	{
		doubleClickListener=new DoubleClickListener();
		tblDates.addMouseListener(doubleClickListener);
	}

	protected void removeComponentListeners()
	{
		tblDates.removeMouseListener(doubleClickListener);
	}

	public void dispose()
	{
		if (airdatesListener!=null) IDObject.removeClassObserver(airdatesListener);
		tmDates.clear();
		super.dispose();
	}

	private class AirdatesListener implements ClassObserver
	{
		public void instanceCreated(DBObject dbObject)
		{
		}

		public void instanceChanged(PropertyChangeEvent event)
		{
			Airdate airdate=(Airdate)event.getSource();
			if (tmDates.containsObject(airdate))
			{
				if (!filter.filter(airdate)) tmDates.removeRow(airdate);
			}
			else
			{
				if (filter.filter(airdate)) tmDates.addRow(new AirdatesTableRow(airdate));
			}
		}
	}

	private class DoubleClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				int rowIndex=tblDates.rowAtPoint(e.getPoint());
				SortableTableRow row=tmDates.getRow(rowIndex);
				if (row!=null) AirdateDetailsView.create((Airdate)row.getUserObject());
				e.consume();
			}
			if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
			{
				int[] rows=tblDates.getSelectedRows();
				Set dates=new HashSet();
				for (int i=0; i<rows.length; i++) dates.add(tmDates.getObject(rows[i]));
				JPopupMenu popupMenu=new JPopupMenu();
				popupMenu.add(new NewAirdateAction());
				popupMenu.add(new DeleteAirdateAction(dates));
				popupMenu.addSeparator();
				popupMenu.add(new NewEpisodeAction(dates));
				popupMenu.add(new UpdateEpisodesAction());
				popupMenu.add(new SplitAction(dates));
				popupMenu.show(tblDates, e.getX(), e.getY());
				e.consume();
			}
			super.mouseClicked(e);
		}
	}

	private static class AirdatesTableModel extends SortableTableModel<Airdate>
	{
		private static final String[] COLUMNS={"time", "channel", "event"};

		public int getColumnCount()
		{
			return COLUMNS.length;
		}

		public String getColumnName(int column)
		{
			return COLUMNS[column];
		}
	}

	private static class AirdatesTableRow extends SortableTableRow implements PropertyChangeListener
	{
		public AirdatesTableRow(Airdate airdate)
		{
			super(airdate);
		}

		public void installListener()
		{
			((Airdate)getUserObject()).addPropertyChangeListener(this);
		}

		public void removeListener()
		{
			((Airdate)getUserObject()).removePropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (((Airdate)getUserObject()).getState()==IDObject.State.DELETED) fireRowDeleted();
			fireRowUpdated();
		}

		public Comparable getSortValue(int column, String property)
		{
			if (column==0)
			{
				Airdate airdate=(Airdate)getUserObject();
				return airdate.getDate();
			}
			return super.getSortValue(column, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			Airdate airdate=(Airdate)getUserObject();
			switch (column)
			{
				case 0:
					DateFormat dateFormat=DateFormat.getDateTimeInstance();
					if (airdate.getDate()!=null) return dateFormat.format(airdate.getDate());
					break;
				case 1:
					return airdate.getChannelName();
				case 2:
					return airdate.getName();
			}
			return "";
		}
	}

	private static class NewAirdateAction extends AbstractAction
	{
		public NewAirdateAction()
		{
			super("Neu");
		}

		public void actionPerformed(ActionEvent e)
		{
			AirdateDetailsView.create((Show)null);
		}
	}

	private class UpdateEpisodesAction extends AbstractAction
	{
		public UpdateEpisodesAction()
		{
			super("Setze Episoden");
		}

		public void actionPerformed(ActionEvent e)
		{
			new ProgressDialog((JFrame)AirdatesView.this.getTopLevelAncestor(), new EpisodeUpdater(tmDates.getObjects())).show();
		}
	}

	private static class NewEpisodeAction extends AbstractAction
	{
		private Airdate airdate;

		public NewEpisodeAction(Collection airdates)
		{
			super("Erzeuge Episode");
			if (airdates.size()==1) airdate=(Airdate)airdates.iterator().next();
			setEnabled(airdate!=null && airdate.getEpisode()==null && airdate.getShow()!=null && !StringUtils.isEmpty(airdate.getEvent()));
		}

		public void actionPerformed(ActionEvent e)
		{
			EpisodeDetailsView.create(airdate);
		}
	}

	private class SplitAction extends AbstractAction
	{
		private Airdate airdate;

		public SplitAction(Collection airdates)
		{
			super("Teile Sendetermin");
			if (airdates.size()==1) airdate=(Airdate)airdates.iterator().next();
			setEnabled(airdate!=null && airdate.getEpisode()==null && airdate.getShow()!=null && !StringUtils.isEmpty(airdate.getEvent()));
		}

		public void actionPerformed(ActionEvent e)
		{
			String event=airdate.getEvent();
			String[] events=event.split("/");
			if (events.length>1)
			{
				Show show=airdate.getShow();
				int length=show.getDefaultEpisodeLength();
				if (length>20 && length<=25) length=30;
				else if (length>40 && length<=45) length=60;
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					for (int i=1; i<events.length; i++)
					{
						Airdate newAirdate=new Airdate();
						newAirdate.setShow(airdate.getShow());
						newAirdate.setEvent(events[i].trim());
						newAirdate.setChannel(airdate.getChannel());
						newAirdate.setDate(new Date(airdate.getDate().getTime()+i*length*DateUtils.MINUTE));
						newAirdate.setLanguage(airdate.getLanguage());
					}
					airdate.setEvent(events[0].trim());
					transaction.close();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					try
					{
						if (transaction!=null) transaction.rollback();
					}
					catch (SQLException e2)
					{
						e2.printStackTrace();
					}
					JOptionPane.showMessageDialog(AirdatesView.this, e1.getLocalizedMessage(), "Ausnahmefehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class DeleteAirdateAction extends AbstractAction
	{
		private Collection airdates;

		public DeleteAirdateAction(Collection airdates)
		{
			super("Löschen");
			this.airdates=airdates;
			setEnabled(!airdates.isEmpty());
		}

		public void actionPerformed(ActionEvent e)
		{
			Iterator it=airdates.iterator();
			while (it.hasNext())
			{
				Airdate airdate=(Airdate)it.next();
				if (airdate.isUsed())
				{
					JOptionPane.showMessageDialog(AirdatesView.this,
												  "Die Sendetermine '"+airdate.getName()+"' kann nicht gelöscht werden.",
												  "Meldung",
												  JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			int option=JOptionPane.showConfirmDialog(AirdatesView.this,
													 "Sendetermine wirklick löschen?",
													 "Löschen?",
													 JOptionPane.YES_NO_OPTION,
													 JOptionPane.QUESTION_MESSAGE);
			if (option==JOptionPane.YES_OPTION)
			{
				Transaction transaction=null;
				try
				{
					transaction=DBSession.getInstance().createTransaction();
					it=airdates.iterator();
					while (it.hasNext())
					{
						Airdate airdate=(Airdate)it.next();
						airdate.delete();
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
					JOptionPane.showMessageDialog(AirdatesView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private static class ShowFilter implements ObjectFilter
	{
		private Show show;

		public ShowFilter(Show show)
		{
			this.show=show;
		}

		public boolean filter(Object object)
		{
			if (object instanceof Airdate)
			{
				Airdate airdate=(Airdate)object;
				if (airdate.getShow()==show) return true;
			}
			return false;
		}
	}

	public boolean isBookmarkable()
	{
		return true;
	}

	public Bookmark getBookmark()
	{
		Bookmark bookmark=new Bookmark(getName(), AirdatesView.class);
		if (show!=null) bookmark.setParameter("show", String.valueOf(show.getId()));
		else
		{
			bookmark.setParameter("unit", Integer.toString(unit));
			bookmark.setParameter("quantity", Integer.toString(quantity));
		}
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String showId=bookmark.getParameter("show");
		if (showId!=null)
		{
			Show show=ShowManager.getInstance().getShow(new Long(showId));
			frame.setCurrentView(new AirdatesView(show), true);
		}
		else
		{
			int unit=Integer.parseInt(bookmark.getParameter("unit"));
			int quantity=Integer.parseInt(bookmark.getParameter("quantity"));
			frame.setCurrentView(new AirdatesView(unit, quantity), true);
		}
	}
}
