package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.*;
import com.kiwisoft.media.movie.Movie;
import com.kiwisoft.media.person.*;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.utils.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan Stiller
 * @since 27.02.11
 */
public class SaveMovieDataTransactional implements Transactional
{
	private MovieData movieData;

	public SaveMovieDataTransactional(MovieData movieData)
	{
		this.movieData=movieData;
	}

	@Override
	public void run() throws Exception
	{
		checkLanguages();
		checkCountries();
		checkPersons(movieData.getCast());
		checkPersons(movieData.getCrew());

		Movie movie;
		if (movieData.getMovies()==null) ImportUtils.matchMovie(movieData);
		if (movieData.getMovies().isEmpty()) movie=new Movie();
		else if (movieData.getMovies().size()==1) movie=movieData.getMovies().iterator().next();
		else throw new RuntimeException("Multiple movies for "+movieData.getTitle()+" found.");

		if (StringUtils.isEmpty(movie.getImdbKey())) movie.setImdbKey(movieData.getImdbKey());
		if (StringUtils.isEmpty(movie.getTitle())) movie.setTitle(movieData.getTitle());
		if (StringUtils.isEmpty(movie.getIndexBy())) movie.setIndexBy(IndexByUtils.createIndexBy(movie.getTitle()));
		if (StringUtils.isEmpty(movie.getGermanTitle())) movie.setGermanTitle(movieData.getGermanTitle());
		movie.setSummaryText(LanguageManager.ENGLISH, movieData.getSummary());
		if (movie.getYear()==null) movie.setYear(movieData.getYear());
		if (movie.getRuntime()==null) movie.setRuntime(movieData.getRuntime());

		Set<Language> languages=new HashSet<Language>();
		for (LanguageData languageData : movieData.getLanguages()) languages.addAll(languageData.getLanguages());
		if (!languages.isEmpty()) movie.setLanguages(languages);

		Set<Country> countries=new HashSet<Country>();
		for (CountryData countryData : movieData.getCountries()) countries.addAll(countryData.getCountries());
		if (!countries.isEmpty()) movie.setCountries(countries);

		Set<CrewData> crew=new HashSet<CrewData>();
		for (Credit crewMember : movie.getCredits()) crew.add(new CrewData(crewMember));
		for (CrewData crewData : movieData.getCrew())
		{
			if (!crew.contains(crewData))
			{
				Credit crewMember=new Credit();
				crewMember.setMovie(movie);
				if (crewData.getPersons().size()==1) crewMember.setPerson(crewData.getPersons().iterator().next());
				crewMember.setCreditType(crewData.getType());
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
				CastMember castMember=new CastMember();
				castMember.setMovie(movie);
				if (castData.getPersons().size()==1) castMember.setActor(castData.getPersons().iterator().next());
				castMember.setCreditType(CreditType.MAIN_CAST);
				castMember.setCharacterName(castData.getRole());
				castMember.setCreditOrder(castData.getCreditOrder());
				cast.add(castData);
			}
		}
	}

	private void checkLanguages()
	{
		for (LanguageData languageData : movieData.getLanguages())
		{
			Set<Language> languages=languageData.getLanguages();
			if (languages==null || languages.isEmpty()) // Rematch to find freshly created objects
			{
				ImportUtils.matchLanguage(languageData);
				languages=languageData.getLanguages();
			}
			if (languages.isEmpty())
			{
				if (StringUtils.isEmpty(languageData.getSymbol())) throw new RuntimeException("Symbol for new language "+languageData.getName()+" missing.");
				else
				{
					Language language=LanguageManager.getInstance().createLanguage(languageData.getSymbol().toLowerCase(), languageData.getName());
					languageData.setLanguages(Collections.singleton(language));
					DBLoader.getInstance().flush();
				}
			}
			else if (languages.size()>2) throw new RuntimeException("Multiple languages for "+languageData.getName()+" found.");
		}
	}

	private void checkCountries()
	{
		for (CountryData countryData : movieData.getCountries())
		{
			Set<Country> countrys=countryData.getCountries();
			if (countrys==null || countrys.isEmpty()) // Rematch to find freshly created objects
			{
				ImportUtils.matchCountry(countryData);
				countrys=countryData.getCountries();
			}
			if (countrys.isEmpty())
			{
				if (StringUtils.isEmpty(countryData.getSymbol())) throw new RuntimeException("Symbol for new country "+countryData.getName()+" missing.");
				else
				{
					Country country=CountryManager.getInstance().createCountry(countryData.getSymbol().toLowerCase(), countryData.getName());
					countryData.setCountries(Collections.singleton(country));
					DBLoader.getInstance().flush();
				}
			}
			else if (countrys.size()>2) throw new RuntimeException("Multiple countrys for "+countryData.getName()+" found.");
		}
	}

	private void checkPersons(List<? extends CreditData> credits)
	{
		for (CreditData creditData : credits)
		{
			if (!StringUtils.isEmpty(creditData.getName()))
			{
				Set<Person> persons=creditData.getPersons();
				if (persons==null || persons.isEmpty()) // Rematch to find freshly created objects
				{
					ImportUtils.matchPerson(creditData, ImportUtils.KeyType.IMDB);
					persons=creditData.getPersons();
				}
				if (persons.isEmpty())
				{
					Person person=PersonManager.getInstance().createPerson();
					person.setName(creditData.getName());
					person.setImdbKey(creditData.getKey());
					creditData.setPersons(Collections.singleton(person));
					DBLoader.getInstance().flush();
				}
				else if (persons.size()>2) throw new RuntimeException("Multiple persons for "+creditData.getName()+" found.");
				else
				{
					Person person=persons.iterator().next();
					if (StringUtils.isEmpty(person.getImdbKey()) && !StringUtils.isEmpty(creditData.getKey()))
					{
						person.setImdbKey(creditData.getKey());
						DBLoader.getInstance().flush();
					}
				}
			}
		}
	}

	@Override
	public void handleError(Throwable throwable, boolean rollback)
	{
		throw new RuntimeException(throwable);
	}
}
