package com.kiwisoft.media.person;

import java.util.Map;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class PersonDetailsAction extends PersonAction
{
	private static final long serialVersionUID=6615382046797116533L;

	private Credits<CastMember> actingCredits;
	private Map<CreditType, Credits<Credit>> creditMap;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		Person person=getPerson();
		if (person!=null)
		{
			actingCredits=person.getSortedActingCredits();
			creditMap=person.getSortedCrewCredits();
		}
		return super.execute();
	}

	public Credits<CastMember> getActingCredits()
	{
		return actingCredits;
	}

	public Map<CreditType, Credits<Credit>> getCreditMap()
	{
		return creditMap;
	}
}
