package com.kiwisoft.media.show;

import com.kiwisoft.media.files.MediaFile;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.media.person.Credit;
import com.kiwisoft.media.person.CastMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 07.10.2009
 */
public class EpisodeDetailsAction extends EpisodeAction
{
	private List<Summary> summaries;
	private Set<Credit> writers;
	private Set<Credit> directors;
	private Set<CastMember> mainCast;
	private Set<CastMember> recurringCast;
	private Set<CastMember> guestCast;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		Episode episode=getEpisode();
		if (episode!=null)
		{
			summaries=new ArrayList<Summary>(episode.getSummaries());
			Collections.sort(summaries, new SummaryComparator());
			writers=episode.getCredits(CreditType.WRITER);
			directors=episode.getCredits(CreditType.DIRECTOR);
			mainCast=episode.getCastMembers(CreditType.MAIN_CAST);
			recurringCast=episode.getCastMembers(CreditType.RECURRING_CAST);
			guestCast=episode.getCastMembers(CreditType.GUEST_CAST);
		}
		return SUCCESS;
	}

	@Override
	public MediaFile findLogo()
	{
		MediaFile logo=null;
		if (getSeason()!=null) logo=getSeason().getLogo();
		if (logo==null) logo=super.findLogo();
		return logo;
	}

	public List<Summary> getSummaries()
	{
		return summaries;
	}

	public Set<Credit> getWriters()
	{
		return writers;
	}

	public Set<Credit> getDirectors()
	{
		return directors;
	}

	public Set<CastMember> getMainCast()
	{
		return mainCast;
	}

	public Set<CastMember> getRecurringCast()
	{
		return recurringCast;
	}

	public Set<CastMember> getGuestCast()
	{
		return guestCast;
	}
}
