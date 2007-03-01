package com.kiwisoft.media;

import java.util.Comparator;

public class AirdateComparator implements Comparator<Airdate>
{
	public final static int TIME=1;
	public final static int EVENT=2;
	public final static int CHANNEL=3;
	public final static int INV_TIME=11;

	private int type;

	public AirdateComparator()
	{
		type=TIME;
	}

	public AirdateComparator(int type)
	{
		this.type=type;
	}

	public int compare(Airdate date1, Airdate date2)
	{
		switch (type)
		{
			case TIME:
				if (date1.getDate().before(date2.getDate())) return 1;
				if (date1.getDate().after(date2.getDate())) return -1;
				return 0;
			case INV_TIME:
				if (date1.getDate().before(date2.getDate())) return -1;
				if (date1.getDate().after(date2.getDate())) return 1;
				return 0;
			case CHANNEL:
				if (date1.getChannel()!=null && date2.getChannel()!=null)
					return date1.getChannel().getName().compareToIgnoreCase(date2.getChannel().getName());
				return 0;
			case EVENT:
				return date1.getEvent().compareTo(date2.getEvent());

		}
		return 0;
	}

}