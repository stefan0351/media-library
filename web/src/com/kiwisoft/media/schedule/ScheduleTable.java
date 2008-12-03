package com.kiwisoft.media.schedule;

import java.util.*;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.media.DateRange;
import com.kiwisoft.media.AirdateManager;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.web.SortableWebTable;
import com.kiwisoft.web.TableSortDescription;
import com.kiwisoft.web.TableConstants;

/**
 * @author Stefan Stiller
 */
public class ScheduleTable extends SortableWebTable<Airdate>
{
	public static final String TIME="time";
	public static final String CHANNEL="channel";
	public static final String EVENT="event";

	private Show show;
	private Person person;

	public ScheduleTable(DateRange range)
	{
		super(TIME, CHANNEL, EVENT);
		init(range);
	}

	public ScheduleTable(Show show, DateRange range)
	{
		super(TIME, CHANNEL, EVENT);
		this.show=show;
		init(range);
	}

	public ScheduleTable(Person person, DateRange range)
	{
		super(TIME, CHANNEL, EVENT);
		this.person=person;
		init(range);
	}

	private void init(DateRange range)
	{
		Date[] dates=range.calculateDates();
		if (dates!=null)
		{
			Set<Airdate> airdates;
			if (show!=null) airdates=AirdateManager.getInstance().getAirdates(show, dates[0], dates[1]);
			else if (person!=null) airdates=AirdateManager.getInstance().getAirdates(person, dates[0], dates[1]);
			else airdates=AirdateManager.getInstance().getAirdates(dates[0], dates[1]);
			for (Airdate airdate : airdates)
			{
				addRow(new AirdateRow(airdate));
			}
		}
		setSortColumn(new TableSortDescription(0, TableConstants.ASCEND));
	}


	@Override
	public Map<String, Object> getContext()
	{
		Map<String, Object> context=new HashMap<String, Object>();
		if (show!=null) context.put(Show.class.getName(), show);
		return context;
	}

	public ResourceBundle getBundle()
	{
		return ResourceBundle.getBundle(ScheduleTable.class.getName());
	}

	private static class AirdateRow extends Row<Airdate>
	{
		private AirdateRow(Airdate airdate)
		{
			super(airdate);
		}

		@Override
		public String getRendererVariant(int columnIndex, String property)
		{
			if (TIME.equals(property)) return "schedule";
			return super.getRendererVariant(columnIndex, property);
		}

		public Object getDisplayValue(int column, String property)
		{
			if (TIME.equals(property)) return getUserObject().getDate();
			else if (CHANNEL.equals(property)) return getUserObject().getChannel();
			else if (EVENT.equals(property)) return getUserObject();
			return "";
		}
	}
}
