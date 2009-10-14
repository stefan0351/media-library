package com.kiwisoft.media.common;

import com.kiwisoft.media.DateRange;
import com.kiwisoft.media.schedule.ScheduleTable;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public interface ScheduleAction
{
	public DateRange getRange();

	public ScheduleTable getScheduleTable();

}
