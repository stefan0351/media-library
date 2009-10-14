package com.kiwisoft.media;

import java.util.*;

import com.kiwisoft.utils.CalendarUtils;

/**
 * @author Stefan Stiller
 */
public abstract class DateRange
{
	private final static Map<Long, DateRange> map=new LinkedHashMap<Long, DateRange>();

	public static final DateRange TODAY=new DateRange(1L, "Today")
	{
		@Override
		public Date[] calculateDates()
		{
			Calendar calendar=Calendar.getInstance();
			CalendarUtils.setStartOfDay(calendar);
			Date date1=calendar.getTime();
			calendar.add(Calendar.HOUR_OF_DAY, 24);
			Date date2=calendar.getTime();
			return new Date[]{date1, date2};
		}
	};
	public static final DateRange NEXT_24_HOURS=new DateRange(2L, "Next 24 Hours")
	{
		@Override
		public Date[] calculateDates()
		{
			Calendar calendar=Calendar.getInstance();
			Date date1=calendar.getTime();
			calendar.add(Calendar.HOUR_OF_DAY, 24);
			Date date2=calendar.getTime();
			return new Date[]{date1, date2};
		}
	};
	public static final DateRange NEXT_3_DAYS=new DateRange(3L, "Next 3 Days")
	{
		@Override
		public Date[] calculateDates()
		{
			Calendar calendar=Calendar.getInstance();
			Date date1=calendar.getTime();
			calendar.add(Calendar.DATE, 3);
			Date date2=calendar.getTime();
			return new Date[]{date1, date2};
		}
	};
	public static final DateRange NEXT_7_DAYS=new DateRange(4L, "Next 7 Days")
	{
		@Override
		public Date[] calculateDates()
		{
			Calendar calendar=Calendar.getInstance();
			Date date1=calendar.getTime();
			calendar.add(Calendar.DATE, 7);
			Date date2=calendar.getTime();
			return new Date[]{date1, date2};
		}
	};
	public static final DateRange CURRENT_WEEK=new DateRange(5L, "Current Week")
	{
		@Override
		public Date[] calculateDates()
		{
			Calendar calendar=Calendar.getInstance();
			CalendarUtils.setStartOfWeek(calendar);
			Date date1=calendar.getTime();
			calendar.add(Calendar.DATE, 7);
			Date date2=calendar.getTime();
			return new Date[]{date1, date2};
		}
	};
	public static final DateRange NEXT_WEEK=new DateRange(6L, "Next Week")
	{
		@Override
		public Date[] calculateDates()
		{
			Calendar calendar=Calendar.getInstance();
			CalendarUtils.setEndOfWeek(calendar);
			Date date1=calendar.getTime();
			calendar.add(Calendar.DATE, 7);
			Date date2=calendar.getTime();
			return new Date[]{date1, date2};
		}
	};
	public static final DateRange CUSTOM=new DateRange(100L, "Custom")
	{
		@Override
		public Date[] calculateDates()
		{
			return null;
		}
	};

	public static DateRange get(Long id)
	{
		return map.get(id);
	}

	public static Collection<DateRange> values()
	{
		return Collections.unmodifiableCollection(map.values());
	}

	private Long id;
	private String name;

	protected DateRange(Long id, String name)
	{
		this.id=id;
		this.name=name;
		map.put(id, this);
	}

	public Long getId()
	{
		return id;
	}

	public abstract Date[] calculateDates();

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (!(o instanceof DateRange)) return false;

		DateRange dateRange=(DateRange) o;

		if (id!=null ? !id.equals(dateRange.id) : dateRange.id!=null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result=id!=null ? id.hashCode() : 0;
		result=31*result+(name!=null ? name.hashCode() : 0);
		return result;
	}
}
