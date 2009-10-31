package com.kiwisoft.media.schedule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import com.kiwisoft.app.ApplicationFrame;
import com.kiwisoft.app.Bookmark;
import com.kiwisoft.app.ViewPanel;
import com.kiwisoft.app.ApplicationListenerSupport;
import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.DateRange;
import com.kiwisoft.media.AirdateManager;
import com.kiwisoft.media.dataimport.TVTVDeLoaderAction;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.ShowManager;
import com.kiwisoft.persistence.IDObject;
import com.kiwisoft.utils.*;
import com.kiwisoft.swing.actions.ContextAction;
import com.kiwisoft.swing.table.*;
import com.kiwisoft.swing.lookup.TimeField;
import com.kiwisoft.swing.date.DateField;

public class ScheduleView extends ViewPanel
{
	private Show show;
	private Person person;
	private DateRange range;
	private Date startDate;
	private Date endDate;

	private Filter<Airdate> filter;
	private TableController<Airdate> tableController;
	private JComboBox dateRangeField;
	private DateField startDateField;
	private DateField endDateField;
	private TimeField startTimeField;
	private TimeField endTimeField;

	public ScheduleView()
	{
		this(null, null, null);
	}

	public ScheduleView(Show show)
	{
		this(null, null, null);
		setShow(show);
	}

	public ScheduleView(Person person)
	{
		this(null, null, null);
		setPerson(person);
	}

	private void setPerson(Person person)
	{
		this.person=person;
		setTitle("TV Schedule for "+person.getName());
		filter=new PersonFilter(person);
	}

	public ScheduleView(DateRange range, Date startDate, Date endDate)
	{
		this.range=range;
		this.startDate=startDate;
		this.endDate=endDate;
		if (show!=null)
		{
			setTitle("TV Schedule for "+show.getTitle());
		}
		else setTitle("TV Schedule");
	}

	private void setShow(Show show)
	{
		setTitle("TV Schedule for "+show.getTitle());
		this.show=show;
		filter=new ShowFilter(show);
	}

	@Override
	public JComponent createContentPanel(final ApplicationFrame frame)
	{
		dateRangeField=new JComboBox(DateRange.values().toArray());
		startDateField=new DateField();
		startTimeField=new TimeField();
		endDateField=new DateField();
		endTimeField=new TimeField();

		SortableTableModel<Airdate> model=new DefaultSortableTableModel<Airdate>("time", "channel", "event");

		if (filter!=null)
		{
			getModelListenerList().installClassListener(Airdate.class, new AirdatesListener());
		}

		tableController=new TableController<Airdate>(model, new DefaultTableConfiguration("ScheduleView.airdates", ScheduleView.class, "airdates"))
		{
			@Override
			public List<ContextAction> getToolBarActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new AirdateDetailsAction());
				actions.add(new CreateAirdateAction());
				actions.add(new DeleteAirdateAction(frame));
				actions.add(new PurgeAirdatesAction(frame));
				actions.add(new TVTVDeLoaderAction(frame, "Update"));
				actions.add(new ScheduleUpdateManagerAction(frame));
				return actions;
			}

			@Override
			public List<ContextAction> getContextActions()
			{
				List<ContextAction> actions=new ArrayList<ContextAction>();
				actions.add(new AirdateDetailsAction());
				actions.add(null);
				actions.add(new CreateAirdateAction());
				actions.add(new DeleteAirdateAction(frame));
				actions.add(null);
				actions.add(new CreateEpisodeFromAirdateAction(frame));
				actions.add(new UpdateEpisodesAction(frame));
				actions.add(new SplitAirdateAction(frame));
				return actions;
			}

			@Override
			public ContextAction getDoubleClickAction()
			{
				return new AirdateDetailsAction();
			}
		};

		JPanel parameterPanel=new JPanel(new FlowLayout(FlowLayout.LEADING));
		parameterPanel.add(new JLabel("Date Range:"));
		parameterPanel.add(dateRangeField);
		parameterPanel.add(new JLabel("From:"));
		parameterPanel.add(startDateField);
		parameterPanel.add(startTimeField);
		parameterPanel.add(new JLabel("To:"));
		parameterPanel.add(endDateField);
		parameterPanel.add(endTimeField);

		JPanel panel=new JPanel(new BorderLayout());
		panel.add(parameterPanel, BorderLayout.NORTH);
		panel.add(tableController.createComponent(), BorderLayout.CENTER);
		return panel;
	}


	@Override
	protected void initializeData()
	{
		super.initializeData();
		if (startDate!=null)
		{
			startDateField.setDate(startDate);
			startTimeField.setTime(DateUtils.getTime(startDate, true));
		}
		if (startDate!=null)
		{
			endDateField.setDate(endDate);
			endTimeField.setTime(DateUtils.getTime(endDate, true));
		}
		dateRangeField.setSelectedItem(range!=null ? range : DateRange.NEXT_24_HOURS);
		selectDateRange((DateRange)dateRangeField.getSelectedItem());
		search();
	}

	@Override
	protected void installComponentListeners()
	{
		ApplicationListenerSupport listeners=getComponentListenerList();
		listeners.installActionListener(dateRangeField, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				selectDateRange((DateRange)dateRangeField.getSelectedItem());
				search();
			}
		});
		ActionListener rangeFieldListener=new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				search();
			}
		};
		listeners.installActionListener(startDateField, rangeFieldListener);
		listeners.installActionListener(startTimeField, rangeFieldListener);
		listeners.installActionListener(endDateField, rangeFieldListener);
		listeners.installActionListener(endTimeField, rangeFieldListener);

		tableController.installListeners();
		super.installComponentListeners();
	}

	private void selectDateRange(DateRange range)
	{
		if (range!=null)
		{
			Date[] dates=range.calculateDates();
			boolean editable=dates==null;
			startDateField.setEditable(editable);
			startTimeField.setEditable(editable);
			endDateField.setEditable(editable);
			endTimeField.setEditable(editable);
			if (dates!=null)
			{
				startDateField.setDate(dates[0]);
				startTimeField.setTime(DateUtils.getTime(dates[0], true));
				endDateField.setDate(dates[1]);
				endTimeField.setTime(DateUtils.getTime(dates[1], true));
			}
		}
	}

	private void search()
	{
		Date startDay=startDateField.getDate();
		Time startTime=startTimeField.getTime();
		Date endDay=endDateField.getDate();
		Time endTime=endTimeField.getTime();
		if (startDay!=null && endDay!=null && startTime!=null && endTime!=null)
		{
			Date startDate=DateUtils.merge(startDay, startTime);
			Date endDate=DateUtils.merge(endDay, endTime);
			Set<Airdate> dates;
			if (show!=null) dates=AirdateManager.getInstance().getAirdates(show, startDate, endDate);
			else if (person!=null) dates=AirdateManager.getInstance().getAirdates(person, startDate, endDate);
			else dates=AirdateManager.getInstance().getAirdates(startDate, endDate);
			SortableTableModel<Airdate> tableModel=tableController.getModel();
			tableModel.clear();
			for (Airdate date : dates) tableModel.addRow(new AirdatesTableRow(date));
			tableModel.sort();
		}
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
		tableController.dispose();
		super.dispose();
	}

	private class AirdatesListener implements ClassListener
	{
		@Override
		public void instanceCreated(Object dbObject)
		{
		}

		@Override
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

	private static class AirdatesTableRow extends SortableTableRow<Airdate> implements PropertyChangeListener
	{
		public AirdatesTableRow(Airdate airdate)
		{
			super(airdate);
		}

		@Override
		public void installListener()
		{
			getUserObject().addPropertyChangeListener(this);
		}

		@Override
		public void removeListener()
		{
			getUserObject().removePropertyChangeListener(this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (getUserObject().getState()==IDObject.State.DELETED) fireRowDeleted();
			fireRowUpdated();
		}


		@Override
		public String getCellFormat(int column, String property)
		{
			if ("time".equals(property)) return "schedule";
			return super.getCellFormat(column, property);
		}

		@Override
		public Comparable getSortValue(int column, String property)
		{
			if (column==0)
			{
				return getUserObject().getDate();
			}
			return super.getSortValue(column, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			Airdate airdate=getUserObject();
			switch (column)
			{
				case 0:
					return airdate.getDate();
				case 1:
					return airdate.getChannel();
				case 2:
					return airdate.getName();
			}
			return "";
		}
	}

	private static class ShowFilter implements Filter<Airdate>
	{
		private Show show;

		public ShowFilter(Show show)
		{
			this.show=show;
		}

		@Override
		public boolean filter(Airdate airdate)
		{
			if (airdate.getShow()==show) return true;
			return false;
		}
	}

	private static class PersonFilter implements Filter<Airdate>
	{
		private Person person;

		public PersonFilter(Person person)
		{
			this.person=person;
		}

		@Override
		public boolean filter(Airdate airdate)
		{
			if (airdate.getPersons().contains(person)) return true;
			return false;
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
		Bookmark bookmark=new Bookmark(getTitle(), ScheduleView.class);
		if (show!=null) bookmark.setParameter("show", String.valueOf(show.getId()));
		if (person!=null) bookmark.setParameter("person", String.valueOf(person.getId()));
		DateRange range=(DateRange)dateRangeField.getSelectedItem();
		if (range!=null) bookmark.setParameter("range", String.valueOf(range.getId()));
		if (range==DateRange.CUSTOM)
		{
			Date startDay=startDateField.getDate();
			Time startTime=startTimeField.getTime();
			if (startDay!=null && startTime!=null)
			{
				Date startDate=DateUtils.merge(startDay, startTime);
				bookmark.setParameter("startDate", String.valueOf(startDate.getTime()));
			}
			Date endDay=endDateField.getDate();
			Time endTime=endTimeField.getTime();
			if (endDay!=null && endTime!=null)
			{
				Date endDate=DateUtils.merge(endDay, endTime);
				bookmark.setParameter("endDate", String.valueOf(endDate.getTime()));
			}
		}
		return bookmark;
	}

	public static void open(Bookmark bookmark, ApplicationFrame frame)
	{
		String showId=bookmark.getParameter("show");
		Show show=null;
		if (showId!=null) show=ShowManager.getInstance().getShow(Long.valueOf(showId));
		String personId=bookmark.getParameter("person");
		Person person=null;
		if (personId!=null) person=PersonManager.getInstance().getPerson(Long.valueOf(personId));
		String rangeId=bookmark.getParameter("range");
		DateRange range=null;
		if (rangeId!=null) range=DateRange.get(Long.valueOf(rangeId));
		String startDateString=bookmark.getParameter("startDate");
		Date startDate=startDateString!=null ? new Date(Long.valueOf(startDateString)) : null;
		String endDateString=bookmark.getParameter("endDate");
		Date endDate=endDateString!=null ? new Date(Long.valueOf(endDateString)) : null;
		ScheduleView view=new ScheduleView(range, startDate, endDate);
		if (show!=null) view.setShow(show);
		if (person!=null) view.setPerson(person);
		frame.setCurrentView(view);
	}

}
