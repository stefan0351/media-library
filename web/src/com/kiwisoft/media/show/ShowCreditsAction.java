package com.kiwisoft.media.show;

import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.person.CastComparator;
import com.kiwisoft.utils.Utils;

import java.util.SortedSet;

/**
 * @author Stefan Stiller
 * @since 06.10.2009
 */
public class ShowCreditsAction extends ShowAction
{
	private SortedSet<CastMember> mainCast;
	private SortedSet<CastMember> recurringCast;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		if (getShow()!=null)
		{
			mainCast=Utils.toSortedSet(getShow().getCastMembers(CreditType.MAIN_CAST), new CastComparator());
			recurringCast=Utils.toSortedSet(getShow().getCastMembers(CreditType.RECURRING_CAST), new CastComparator());
		}
		return SUCCESS;
	}

	public SortedSet<CastMember> getMainCast()
	{
		return mainCast;
	}

	public SortedSet<CastMember> getRecurringCast()
	{
		return recurringCast;
	}
}
