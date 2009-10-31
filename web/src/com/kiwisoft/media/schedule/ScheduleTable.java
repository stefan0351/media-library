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
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class ScheduleTable extends SortableWebTable<Airdate>
{
	public static final String TIME="time";
	public static final String CHANNEL="channel";
	public static final String EVENT="event";
	public static final String LINK="link";

	private Show show;
	private Person person;

	public ScheduleTable(DateRange range)
	{
		super(TIME, CHANNEL, EVENT, LINK);
		init(range);
	}

	public ScheduleTable(Show show, DateRange range)
	{
		super(TIME, CHANNEL, EVENT, LINK);
		this.show=show;
		init(range);
	}

	public ScheduleTable(Person person, DateRange range)
	{
		super(TIME, CHANNEL, EVENT, LINK);
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

	@Override
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
			if (LINK.equals(property)) return "html";
			return super.getRendererVariant(columnIndex, property);
		}

		@Override
		public Object getDisplayValue(int column, String property)
		{
			if (TIME.equals(property)) return getUserObject().getDate();
			else if (CHANNEL.equals(property)) return getUserObject().getChannel();
			else if (EVENT.equals(property)) return getUserObject();
			else if (LINK.equals(property))
			{
				if (!StringUtils.isEmpty(getUserObject().getDetailsLink()))
				{
					StringBuilder buffer=new StringBuilder();
					buffer.append("<img");
					buffer.append(" src=\"${contextPath}/file?type=Icon&name=details\"");
					buffer.append(" onClick=\"newWindow('Details', '").append(getUserObject().getDetailsLink()).append("', 500, 500);\"");
					buffer.append(" alt=\"Details\"");
					buffer.append(" border=\"0\">");
					return buffer.toString();
				}
			}
			return "";
		}
	}
}
