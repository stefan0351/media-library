package com.kiwisoft.media.dataImport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kiwisoft.media.Language;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.CrewMember;
import com.kiwisoft.media.person.Person;
import com.kiwisoft.media.person.PersonManager;
import com.kiwisoft.media.person.CastMember;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.db.Transactional;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 06.03.2007
 * Time: 15:37:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class CreateMovieTx implements Transactional
{
	private MovieData movieData;
	private Movie movie;

	protected CreateMovieTx(MovieData movieData)
	{
		this.movieData=movieData;
	}

	public void run() throws Exception
	{
		Language english=LanguageManager.getInstance().getLanguageBySymbol("en");
		Map<String, Person> persons=new HashMap<String, Person>();

		movie=movieData.getMovie();
		if (movie==null) movie=new Movie();

		if (StringUtils.isEmpty(movie.getTitle())) movie.setTitle(movieData.getTitle());
		if (StringUtils.isEmpty(movie.getGermanTitle())) movie.setGermanTitle(movieData.getGermanTitle());
		if (StringUtils.isEmpty(movie.getSummaryText(english))) movie.setSummaryText(english, movieData.getSummary());
		if (movie.getYear()==null) movie.setYear(movieData.getYear());
		if (movie.getRuntime()==null) movie.setRuntime(movieData.getRuntime());
		if (movie.getLanguages().isEmpty()) movie.setLanguages(movieData.getLanguages());
		if (movie.getCountries().isEmpty()) movie.setCountries(movieData.getCountries());

		Set<CrewData> crew=new HashSet<CrewData>();
		for (CrewMember crewMember : movie.getCrewMembers()) crew.add(new CrewData(crewMember));
		for (CrewData crewData : movieData.getCrew())
		{
			if (!crew.contains(crewData))
			{
				Person person=PersonManager.getInstance().getPersonByName(crewData.getName());
				if (person==null) person=persons.get(crewData.getName());
				if (person==null)
				{
					person=new Person();
					person.setName(crewData.getName());
					persons.put(crewData.getName(), person);
				}
				CrewMember crewMember=new CrewMember();
				crewMember.setMovie(movie);
				crewMember.setPerson(person);
				crewMember.setType(crewData.getType());
				crewMember.setSubType(crewData.getSubType());
				crew.add(crewData);
			}
		}

		Set<CastData> cast=new HashSet<CastData>();
		for (CastMember castMember : movie.getCastMembers()) cast.add(new CastData(castMember));
		for (CastData castData : movieData.getCast())
		{
			if (!cast.contains(castData))
			{
				Person person=PersonManager.getInstance().getPersonByName(castData.getActor());
				if (person==null) person=persons.get(castData.getActor());
				if (person==null)
				{
					person=new Person();
					person.setName(castData.getActor());
					persons.put(castData.getActor(), person);
				}
				CastMember castMember=new CastMember();
				castMember.setMovie(movie);
				castMember.setActor(person);
				castMember.setType(CastMember.MAIN_CAST);
				castMember.setCharacterName(castData.getRole());
				castMember.setCreditOrder(castData.getCreditOrder());
				cast.add(castData);
			}
		}
	}

	public Movie getMovie()
	{
		return movie;
	}
}
