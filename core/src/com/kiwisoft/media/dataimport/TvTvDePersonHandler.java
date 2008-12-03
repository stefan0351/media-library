package com.kiwisoft.media.dataimport;

import java.util.Set;

import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.media.Name;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
*/
class TvTvDePersonHandler extends TvTvDeHandler<Person>
{
	TvTvDePersonHandler(TVTVDeLoader loader, Person object)
	{
		super(loader, object);
	}

	protected String getName()
	{
		return getObject().getName();
	}

	protected Set<SearchPattern> getSearchPatterns()
	{
		return SearchManager.getInstance().getSearchPattern(SearchPattern.TVTV, getObject());
	}

	protected boolean deleteAirdates()
	{
		return true;
	}

	protected boolean analyze(TvTvDeAirdateData airdate)
	{
		if (super.analyze(airdate))
		{
			if (containsPerson(airdate))
			{
				airdate.setPerson(getObject());
				return true;
			}
		}
		return false;
	}

	private boolean containsPerson(TvTvDeAirdateData airdate)
	{
		if (airdate.getShow()!=null)
		{
			if (DBLoader.getInstance().count(CastMember.class, null, "show_id=? and actor_id=?",
											 airdate.getShow().getId(), getObject().getId())>0) return true;
		}
		if (airdate.getMovie()!=null)
		{
			if (DBLoader.getInstance().count(CastMember.class, null, "movie_id=? and actor_id=?",
											 airdate.getMovie().getId(), getObject().getId())>0) return true;
		}
		if (!StringUtils.isEmpty(airdate.getCast()))
		{
			String cast=airdate.getCast().toLowerCase();
			if (cast.contains(getObject().getName().toLowerCase())) return true;
			for (Name name : getObject().getAltNames())
			{
				if (cast.contains(name.getName().toLowerCase())) return true;
			}
		}
		return false;
	}
}
