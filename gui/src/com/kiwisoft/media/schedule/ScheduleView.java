package com.kiwisoft.media.schedule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.*;
import javax.swing.*;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.media.utils.TableController;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.AirdateManager;
import com.kiwisoft.utils.Bookmark;
import com.kiwisoft.utils.ClassObserver;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.db.DBObject;
import com.kiwisoft.utils.db.IDObject;
import com.kiwisoft.utils.filter.ObjectFilter;
import com.kiwisoft.utils.gui.ApplicationFrame;
import com.kiwisoft.utils.gui.ViewPanel;
import com.kiwisoft.utils.gui.actions.ContextAction;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.DefaultSortableTableModel;
import com.kiwisoft.utils.gui.table.DefaultTableConfiguration;

public class ScheduleView extends ViewPanel
{
	private Show show;

	private String title;
	private Collection airdates;
	private ObjectFilter filter;
	private AirdatesListener airdatesListener;
	private int unit;
	private int quantity;
	private TableController<Airdate> tableController;

	public ScheduleView(Show show)
	{
		this.show=show;
		this.title="Schedule for "+show.getTitle();
		this.airdates=show.getAirdates();
		this.filter=new ShowFilter(show);
	}

	public ScheduleView(int unit, int quantity)
	{
		this.unit=unit;
		this.quantity=quantity;
		this.airdates=AirdateManager.getInstance().getAirdates(unit, quantity);
		this.title="Current Schedule";
	}

	public String getName()
	{
		return title;
	}

	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		SortableTableModel<Airdate> model=new DefaultSortableTableModel<Airdate>("time", "channel", "event");
		Iterator it=airdates.iterator();
		while (it.hasNext())
		{
			Airdate date=(Airdate)it.next();
			model.addRow(new AirdatesTableRow(date));
		}
		model.sort();
		airdates=null;

		if (filter!=null)
		{
			airdatesListener=new AirdatesListener();
			DBObject.addClassObserver(airdatesListener, Airdate.class);
		}

		tableController=new TableController<Airdate>(model, new DefaultTableConfiguration(ScheduleView.class, "airdates"))
		{
			@Override
			public List<ContextAction<? super Airdate>> getToolBarActions()
			{
				List<ContextAction<? super Airdate>> actions=new ArrayList<ContextAction<? super Airdate>>();
				actions.add(new AirdateDetailsAction());
				actions.add(new NewAirdateAction());
				actions.add(new DeleteAirdateAction(frame));
				actions.add(new CreateEpisodeFromAirdateAction());
				actions.add(new UpdateEpisodesAction(frame));
				actions.add(new SplitAirdateAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction<? super Airdate>> getContextActions()
			{
				List<ContextAction<? super Airdate>> actions=new ArrayList<ContextAction<? super Airdate>>();
				actions.add(new AirdateDetailsAction());
				actions.add(null);
				actions.add(new NewAirdateAction());
				actions.add(new DeleteAirdateAction(frame));
				actions.add(null);
				actions.add(new CreateEpisodeFromAirdateAction());
				actions.add(new UpdateEpisodesAction(frame));
				actions.add(new SplitAirdateAction(frame));
				return actions;
			}

			@Override
			public ContextAction<Airdate> getDoubleClickAction()
			{
				return new AirdateDetailsAction();
			}
		};
		return tableController.createComponent();
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
		if (airdatesListener!=null) IDObject.removeClassObserver(airdatesListener);
		tableController.dispose();
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
			SortableTableModel<Airdate> model=tableController.getModel();
			if (model.containsObject(airdate))
			{
				if (!filter.filter(airdate)) model.removeObject(airdate);
			}
			else
			{
				if (filter.filter(airdate)) model.addRow(new AirdatesTableRow(airdate));
			}
		}
	}

	private static class AirdatesTableRow extends SortableTableRow implements PropertyChangeListener
	{
		private DateFormat dateFormat;

		public AirdatesTableRow(Airdate airdate)
		{
			super(airdate);
			dateFormat=DateFormat.getDateTimeInstance();
			dateFormat.setTimeZone(DateUtils.GMT);
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
		Bookmark bookmark=new Bookmark(getName(), ScheduleView.class);
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
			frame.setCurrentView(new ScheduleView(show), true);
		}
		else
		{
			int unit=Integer.parseInt(bookmark.getParameter("unit"));
			int quantity=Integer.parseInt(bookmark.getParameter("quantity"));
			frame.setCurrentView(new ScheduleView(unit, quantity), true);
		}
	}
}
