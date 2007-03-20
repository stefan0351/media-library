package com.kiwisoft.media.schedule;

import java.util.ResourceBundle;
import java.util.Set;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.DateFormat;

import com.kiwisoft.media.Airdate;
import com.kiwisoft.web.SortableWebTable;
import com.kiwisoft.utils.gui.table.SortableTableRow;
import com.kiwisoft.utils.gui.table.TableSortDescription;
import com.kiwisoft.utils.gui.table.TableConstants;
import com.kiwisoft.utils.DateUtils;
import com.kiwisoft.utils.db.IDObject;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 19.03.2007
 * Time: 20:11:53
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleTable extends SortableWebTable<Airdate>
{
	public static final String TIME="time";
	public static final String CHANNEL="channel";
	public static final String EVENT="event";

	public ScheduleTable()
	{
		super(TIME, CHANNEL, EVENT);
	}

	public void addAll(Set<Airdate> airdates)
	{
		for (Airdate airdate : airdates)
		{
			addRow(new AirdateRow(airdate));
		}
		setSortColumn(new TableSortDescription(0, TableConstants.ASCEND));
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

		public Object getDisplayValue(int column, String property)
		{
			if (TIME.equals(property)) return getUserObject().getDate();
			else if (CHANNEL.equals(property)) return getUserObject().getChannelName();
			else if (EVENT.equals(property)) return getUserObject();
			return "";
		}
	}


}
