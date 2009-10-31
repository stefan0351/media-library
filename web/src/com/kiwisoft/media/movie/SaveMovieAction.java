package com.kiwisoft.media.movie;

import com.kiwisoft.media.*;
import com.kiwisoft.media.person.*;
import com.kiwisoft.media.dataimport.*;
import com.kiwisoft.persistence.DBSession;
import com.kiwisoft.persistence.Transactional;
import com.kiwisoft.persistence.DBLoader;
import com.kiwisoft.utils.StringUtils;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Stefan Stiller
 * @since 11.10.2009
 */
public class SaveMovieAction extends BaseAction
{
	private static final long serialVersionUID=8738088653977122163L;

	private Long movieId;
	private MovieData movieData;
	private boolean returnCode;

	@Override
	public String execute()
	{
		if (movieData!=null)
		{
			validateLanguages();
			validateCountries();
			validateCast();
			validateCrew();
			if (!hasActionErrors())
			{
				DBSession.execute(new SaveMovie());
			}
		}
		else addActionError("No movie data submitted.");
		System.out.println("SaveMovieAction.execute: "+getActionErrors());
		returnCode=!hasActionErrors();
		return SUCCESS;
	}

	private void validateLanguages()
	{
		final LanguageManager languageManager=LanguageManager.getInstance();
		for (final LanguageData languageData : movieData.getLanguages())
		{
			Language language=null;
			if (!StringUtils.isEmpty(languageData.getSymbol()))
			{
				language=languageManager.getLanguageBySymbol(languageData.getSymbol().toLowerCase());
			}
			if (!StringUtils.isEmpty(languageData.getName()))
			{
				if (language==null) language=languageManager.getLanguageByName(languageData.getName());
				else if (!languageData.getName().equalsIgnoreCase(language.getName()))
				{
					addActionError("Language name doesn't match symbol. Expected "+language.getName()+" but was "+languageData.getName()+".");
				}
			}
			if (language==null)
			{
				if (!StringUtils.isEmpty(languageData.getSymbol()) && !StringUtils.isEmpty(languageData.getName()))
				{
					CreateLanguage createLanguage=new CreateLanguage(languageData);
					if (DBSession.execute(createLanguage)) language=createLanguage.getLanguage();
				}
				else addActionError("Language "+languageData.getName()+" doesn't exist. Please enter the ISO symbol if it should be created.");
			}
			languageData.setLanguage(language);
		}
	}

	private void validateCountries()
	{
		final CountryManager countryManager=CountryManager.getInstance();
		for (final CountryData countryData : movieData.getCountries())
		{
			Country country=null;
			if (!StringUtils.isEmpty(countryData.getSymbol()))
			{
				country=countryManager.getCountryBySymbol(countryData.getSymbol().toUpperCase());
			}
			if (!StringUtils.isEmpty(countryData.getName()))
			{
				if (country==null) country=countryManager.getCountryByName(countryData.getName());
				else if (!countryData.getName().equalsIgnoreCase(country.getName()))
				{
					addActionError("Country name doesn't match symbol. Expected "+country.getName()+" but was "+countryData.getName()+".");
				}
			}
			if (country==null)
			{
				if (!StringUtils.isEmpty(countryData.getSymbol()) && !StringUtils.isEmpty(countryData.getName()))
				{
					CreateCountry createCountry=new CreateCountry(countryData);
					if (DBSession.execute(createCountry)) country=createCountry.getCountry();
				}
				else addActionError("Country "+countryData.getName()+" doesn't exist. Please enter the ISO symbol if it should be created.");
			}
			countryData.setCountry(country);
		}
	}

	private void validateCast()
	{
		for (CastData castMember : movieData.getCast())
		{
			Person person=findPerson(castMember.getImdbKey(), castMember.getActor());
			castMember.setPerson(person);
		}
	}

	private void validateCrew()
	{
		for (CrewData crewMember : movieData.getCrew())
		{
			Person person=findPerson(crewMember.getImdbKey(), crewMember.getName());
			crewMember.setPerson(person);
		}
	}

	private Person findPerson(String imdbKey, String name)
	{
		Person person=null;
		if (!StringUtils.isEmpty(imdbKey))
		{
			person=PersonManager.getInstance().getPersonByIMDbKey(imdbKey);
		}
		if (person==null && !StringUtils.isEmpty(name))
		{
			Set<Person> persons=DBLoader.getInstance().loadSet(Person.class, null, "binary name=?"+(imdbKey!=null ? " and imdb_key is null" : ""), name);
			if (!persons.isEmpty())
			{
				if (persons.size()==1) person=persons.iterator().next();
				else
				{
					addActionError("Multiple persons with name '"+name+"' found. Set the IMDb key for the correct person to '"+imdbKey+"' and try again.");
					return null;
				}
			}
		}
		if (person==null && !StringUtils.isEmpty(name))
		{
			CreatePerson createPerson=new CreatePerson(name, imdbKey);
			if (DBSession.execute(createPerson)) person=createPerson.getPerson();
		}
		else if (person!=null && !StringUtils.isEmpty(imdbKey) && StringUtils.isEmpty(person.getImdbKey()))
		{
			DBSession.execute(new UpdatePerson(person, imdbKey));
		}
		return person;
	}

	public boolean getReturnCode()
	{
		return returnCode;
	}

	public MovieData getMovieData()
	{
		return movieData;
	}

	public void setMovieData(MovieData movieData)
	{
		this.movieData=movieData;
	}

	public Long getMovieId()
	{
		return movieId;
	}

	public void setMovieId(Long movieId)
	{
		this.movieId=movieId;
	}

	private class CreateLanguage implements Transactional
	{
		private final LanguageData languageData;
		private Language language;

		public CreateLanguage(LanguageData languageData)
		{
			this.languageData=languageData;
		}

		@Override
		public void run() throws Exception
		{
			language=LanguageManager.getInstance().createLanguage(languageData.getSymbol().toLowerCase(), languageData.getName());
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			addActionError(throwable.getMessage());
		}

		public Language getLanguage()
		{
			return language;
		}
	}

	private class CreateCountry implements Transactional
	{
		private final CountryData countryData;
		private Country country;

		public CreateCountry(CountryData countryData)
		{
			this.countryData=countryData;
		}

		@Override
		public void run() throws Exception
		{
			country=CountryManager.getInstance().createCountry(countryData.getSymbol().toUpperCase(), countryData.getName());
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			addActionError(throwable.getMessage());
		}

		public Country getCountry()
		{
			return country;
		}
	}

	private class CreatePerson implements Transactional
	{
		private String name;
		private String imdbKey;
		private Person person;

		public CreatePerson(String name, String imdbKey)
		{
			this.name=name;
			this.imdbKey=imdbKey;
		}

		@Override
		public void run() throws Exception
		{
			person=PersonManager.getInstance().createPerson();
			person.setName(name);
			person.setImdbKey(imdbKey);
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			addActionError(throwable.getMessage());
		}

		public Person getPerson()
		{
			return person;
		}
	}

	private class UpdatePerson implements Transactional
	{
		private String imdbKey;
		private Person person;

		public UpdatePerson(Person person, String imdbKey)
		{
			this.person=person;
			this.imdbKey=imdbKey;
		}

		@Override
		public void run() throws Exception
		{
			person.setImdbKey(imdbKey);
		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			addActionError(throwable.getMessage());
		}
	}

	private class SaveMovie implements Transactional
	{
		@Override
		public void run() throws Exception
		{
			Movie movie;
			if (movieId!=null) movie=MovieManager.getInstance().getMovie(movieId);
			else
			{
				movie=new Movie();
				movieId=movie.getId();
			}

			if (StringUtils.isEmpty(movie.getImdbKey())) movie.setImdbKey(movieData.getImdbKey());
			if (StringUtils.isEmpty(movie.getTitle())) movie.setTitle(movieData.getTitle());
			if (StringUtils.isEmpty(movie.getIndexBy())) movie.setIndexBy(IndexByUtils.createIndexBy(movie.getTitle()));
			if (StringUtils.isEmpty(movie.getGermanTitle())) movie.setGermanTitle(movieData.getGermanTitle());
			String oldSummary=movie.getSummaryText(LanguageManager.ENGLISH);
			if (StringUtils.isEmpty(oldSummary) || oldSummary.length()<movieData.getSummary().length())
				movie.setSummaryText(LanguageManager.ENGLISH, movieData.getSummary());
			if (movie.getYear()==null) movie.setYear(movieData.getYear());
			if (movie.getRuntime()==null) movie.setRuntime(movieData.getRuntime());

			Set<Language> languages=new HashSet<Language>();
			for (LanguageData languageData : movieData.getLanguages()) languages.add(languageData.getLanguage());
        	if (!languages.isEmpty()) movie.setLanguages(languages);

			Set<Country> countries=new HashSet<Country>();
			for (CountryData countryData : movieData.getCountries()) countries.add(countryData.getCountry());
			if (!countries.isEmpty()) movie.setCountries(countries);

			Set<CrewData> crew=new HashSet<CrewData>();
			for (Credit crewMember : movie.getCredits()) crew.add(new CrewData(crewMember));
			for (CrewData crewData : movieData.getCrew())
			{
				if (!crew.contains(crewData))
				{
					Credit crewMember=new Credit();
					crewMember.setMovie(movie);
					crewMember.setPerson(crewData.getPerson());
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
					castMember.setActor(castData.getPerson());
					castMember.setCreditType(CreditType.MAIN_CAST);
					castMember.setCharacterName(castData.getRole());
					castMember.setCreditOrder(castData.getCreditOrder());
					cast.add(castData);
				}
			}

		}

		@Override
		public void handleError(Throwable throwable, boolean rollback)
		{
			addActionError(throwable.getMessage());
		}
	}
}
