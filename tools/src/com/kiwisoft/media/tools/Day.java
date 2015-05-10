/*
 * TV-Browser
 * Copyright (C) 04-2003 Martin Oberhauser (darras@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date: 2008-08-07 14:25:33 +0200 (Thu, 07 Aug 2008) $
 *   $Author: ds10 $
 * $Revision: 4892 $
 */

/**
 * TV-Browser
 * @author Martin Oberhauser
 */

package com.kiwisoft.media.tools;

import java.io.*;
import java.util.Calendar;

public class Day implements Comparable<Day>
{

	private final int mYear;

	private final int mMonth;

	private final int mDay;

	/**
	 * Constructs a new Date object, initialized with the current date.
	 */
	public Day()
	{
		Calendar mCalendar=Calendar.getInstance();
		mYear=mCalendar.get(Calendar.YEAR);
		mMonth=mCalendar.get(Calendar.MONTH)+1;
		mDay=mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public Day(int year, int month, int dayOfMonth)
	{
		mYear=year;
		mMonth=month;
		mDay=dayOfMonth;
	}

	public static Day createYYYYMMDD(String date, String separator)
	{
		if (date==null)
			return null;
		String[] s=date.split(separator);
		if (s.length!=3)
			return null;
		int year=Integer.parseInt(s[0]);
		int month=Integer.parseInt(s[1]);
		int day=Integer.parseInt(s[2]);
		return new Day(year, month, day);

	}

	public static Day createDDMMYYYY(String date, String separator)
	{
		if (date==null)
			return null;
		String[] s=date.split(separator);
		if (s.length!=3)
			return null;
		int day=Integer.parseInt(s[0]);
		int month=Integer.parseInt(s[1]);
		int year=Integer.parseInt(s[2]);
		return new Day(year, month, day);

	}

	public static Day createYYMMDD(String date, String separator)
	{
		return createYYYYMMDD("20"+date, separator);
	}

	/**
	 * Attention: DO NOT USE THIS!
	 * Under Os/2 it has some problems with calculating the real Date!
	 *
	 * @deprecated
	 */
	public Day(int daysSince1970)
	{

		long l=(long) daysSince1970*24*60*60*1000;
		java.util.Date d=new java.util.Date(l);
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		mYear=cal.get(Calendar.YEAR);
		mMonth=cal.get(Calendar.MONTH)+1;
		mDay=cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @param d The date to check for days since.
	 * @return The days since the given date.
	 * @since 0.9.7.4 This method may not return the exactly number of days since
	 *        the calculation is confounded by daylight savings time
	 *        switchovers... Around midnight the result may not be correct.
	 */

	public int getNumberOfDaysSince(Day d)
	{
		Calendar cal_1=d.getCalendar();
		java.util.Date utilDate_1=cal_1.getTime();
		long millis_1=utilDate_1.getTime();

		Calendar cal_2=getCalendar();
		java.util.Date utilDate_2=cal_2.getTime();
		long millis_2=utilDate_2.getTime();

		int hours=(int) ((millis_2-millis_1)/1000L/60L/60L);

		if (hours%24>1)
			return (int) (hours/24.+0.99);

		return (int) ((millis_2-millis_1)/1000L/60L/60L/24L);

	}

	public int getYear()
	{
		return mYear;
	}

	public int getMonth()
	{
		return mMonth;
	}

	public int getDayOfMonth()
	{
		return mDay;
	}

	/**
	 * Returns the week number within the current year.
	 *
	 * @return The week number.
	 * @since 2.5.1
	 */
	public int getWeekOfYear()
	{
		Calendar cal=getCalendar();
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public Day(Calendar cal)
	{
		mYear=cal.get(Calendar.YEAR);
		mMonth=cal.get(Calendar.MONTH)+1;
		mDay=cal.get(Calendar.DAY_OF_MONTH);
	}

	public Day(Day d)
	{
		mYear=d.mYear;
		mMonth=d.mMonth;
		mDay=d.mDay;

	}

	public static Day getCurrentDate()
	{
		return new Day();
	}

	/**
	 * Creates a new instance from a RandomAccessFile.
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @since 2.2
	 */
	public Day(DataInput in) throws IOException, ClassNotFoundException
	{
		int version=in.readInt();
		if (version==1)
		{ // currently, version==2 is used
			int date=in.readInt();
			long l=(long) date*24*60*60*1000;
			java.util.Date d=new java.util.Date(l);
			Calendar mCalendar=Calendar.getInstance();
			mCalendar.setTime(d);
			mYear=mCalendar.get(Calendar.YEAR);
			mMonth=mCalendar.get(Calendar.MONTH)+1;
			mDay=mCalendar.get(Calendar.DAY_OF_MONTH);
		}
		else
		{
			mYear=in.readInt();
			mMonth=in.readInt();
			mDay=in.readInt();
		}

	}

	/**
	 * Creates a new instance from a stream.
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Day(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		int version=in.readInt();
		if (version==1)
		{ // currently, version==2 is used
			int date=in.readInt();
			long l=(long) date*24*60*60*1000;
			java.util.Date d=new java.util.Date(l);
			Calendar mCalendar=Calendar.getInstance();
			mCalendar.setTime(d);
			mYear=mCalendar.get(Calendar.YEAR);
			mMonth=mCalendar.get(Calendar.MONTH)+1;
			mDay=mCalendar.get(Calendar.DAY_OF_MONTH);
		}
		else
		{
			mYear=in.readInt();
			mMonth=in.readInt();
			mDay=in.readInt();
		}

	}

	/**
	 * Writes this instance to a RandomAccessFile.
	 *
	 * @param out
	 * @throws IOException
	 * @since 2.2
	 */
	public void writeToDataFile(RandomAccessFile out) throws IOException
	{
		out.writeInt(2); // version
		out.writeInt(mYear);
		out.writeInt(mMonth);
		out.writeInt(mDay);
	}

	/**
	 * Writes this instance to a stream.
	 *
	 * @throws IOException
	 */
	public void writeData(ObjectOutputStream out) throws IOException
	{
		out.writeInt(2); // version
		out.writeInt(mYear);
		out.writeInt(mMonth);
		out.writeInt(mDay);
	}

	/**
	 * A hash code implementation that returns the same code for equal Dates.
	 */

	public int hashCode()
	{
		return mYear*10000+mMonth*100+mDay;

	}

	/**
	 * return the textual representation of this date with abbreviated day of week
	 * and abbreviated month name
	 *
	 * @return date string
	 */
	public String getDateString()
	{
		return String.valueOf(getValue());
	}

	public long getValue()
	{
		return mYear*10000+mMonth*100+mDay;
	}

	public static Day createDateFromValue(long value)
	{
		int year=(int) (value/10000L);
		int month=(int) (value%10000L/100L);
		int day=(int) (value%100L);

		return new Day(year, month, day);
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof Day)
		{
			Day d=(Day) obj;
			return d.getDayOfMonth()==getDayOfMonth() && d.getMonth()==getMonth() && d.getYear()==getYear();
		}
		return false;

	}

	public java.util.Calendar getCalendar()
	{
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MONTH, mMonth-1);
		cal.set(Calendar.YEAR, mYear);
		cal.set(Calendar.DAY_OF_MONTH, mDay);

		return cal;
	}

	/**
	 * @deprecated
	 */
	public int getDaysSince1970()
	{
		Calendar cal=getCalendar();
		int zoneOffset=cal.get(Calendar.ZONE_OFFSET);
		int daylight=cal.get(Calendar.DST_OFFSET);
		java.util.Date utilDate=cal.getTime();
		long millis=utilDate.getTime()+zoneOffset+daylight;
		return (int) (millis/1000L/60L/60L/24L);
	}

	public Day addDays(int days)
	{
		Calendar cal=getCalendar();
		cal.add(Calendar.DAY_OF_MONTH, days);
		return new Day(cal);
	}

	@Override
	public int compareTo(Day otherDate)
	{
		if (this.mYear<otherDate.mYear)
		{
			return -1;
		}
		else if (this.mYear>otherDate.mYear)
		{
			return 1;
		}
		else if (this.mMonth<otherDate.mMonth)
		{
			return -1;
		}
		else if (this.mMonth>otherDate.mMonth)
		{
			return 1;
		}
		else if (this.mDay<otherDate.mDay)
		{
			return -1;
		}
		else if (this.mDay>otherDate.mDay)
		{
			return 1;
		}
		return 0;
	}

	/**
	 * get the day of the week for this date
	 *
	 * @return day of week, as Calendar.MONDAY and so on
	 * @since 2.6
	 */
	public int getDayOfWeek()
	{
		Calendar cal=getCalendar();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * is this a Monday?
	 *
	 * @return <code>true</code>, if this is the first day of the week
	 * @since 2.6
	 */
	public boolean isFirstDayOfWeek()
	{
		return getDayOfWeek()==Calendar.MONDAY;
	}

}