package com.kiwisoft.media.show;

import com.kiwisoft.media.common.ScheduleAction;
import com.kiwisoft.media.DateRange;
import com.kiwisoft.media.schedule.ScheduleTable;

/**
 * @author Stefan Stiller
 * @since 05.10.2009
 */
public class ShowScheduleAction extends ShowAction implements ScheduleAction
{
	private Long rangeId;

	private DateRange range;
	private ScheduleTable scheduleTable;

	@Override
	public String execute() throws Exception
	{
		String result=super.execute();
		if (rangeId!=null) this.range=DateRange.get(Long.valueOf(rangeId));
		if (range==null) range=DateRange.NEXT_24_HOURS;
		scheduleTable=new ScheduleTable(getShow(), range);
		scheduleTable.sort();
		return result;
	}

	public Long getRangeId()
	{
		return rangeId;
	}

	public void setRangeId(Long rangeId)
	{
		this.rangeId=rangeId;
	}

	@Override
	public DateRange getRange()
	{
		return range;
	}

	@Override
	public ScheduleTable getScheduleTable()
	{
		return scheduleTable;
	}
}
