package com.kiwisoft.media.movie;

import com.kiwisoft.media.*;
import com.kiwisoft.media.dataimport.IMDbComLoader;
import com.kiwisoft.media.dataimport.MovieData;
import com.kiwisoft.media.dataimport.LanguageData;
import com.kiwisoft.media.dataimport.CountryData;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.utils.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 * @since 09.10.2009
 */
public class ImportImdbAction extends BaseAction
{
	private static final long serialVersionUID=-600622146129213372L;

	private String url;
	private MovieData movieData;
	private Set<Movie> movies;

	@Override
	public String getPageTitle()
	{
		return "Movies";
	}

	@Override
	public String execute() throws Exception
	{
		Matcher matcher=Pattern.compile("http://(german|www).imdb.(com|de)/title/(\\w+)/.*").matcher(url);
		if (matcher.matches())
		{
			url="http://www.imdb.com/title/"+matcher.group(2)+"/";
			movieData=new IMDbComLoader(url, matcher.group(2)).load();
			System.out.println("ImportImdbAction.execute: movieData = "+movieData);

			fillData();

			MovieManager movieManager=MovieManager.getInstance();
			movies=new LinkedHashSet<Movie>();
			Movie movie=movieManager.getMovieByIMDbKey(movieData.getImdbKey());
			if (movie!=null) movies.add(movie);
			movies.addAll(movieManager.getMoviesByTitle(movieData.getTitle()));
			if (!StringUtils.isEmpty(movieData.getGermanTitle()))
				movies.addAll(movieManager.getMoviesByTitle(movieData.getGermanTitle()));
			return SUCCESS;
		}
		else
		{
			addActionError("Invalid URL.");
			return ERROR;
		}
	}

	private void fillData()
	{
		for (LanguageData languageData : movieData.getLanguages())
		{
			Language language=LanguageManager.getInstance().getLanguageByName(languageData.getName());
			if (language!=null) languageData.setSymbol(language.getSymbol());
		}
		for (CountryData countryData : movieData.getCountries())
		{
			if ("UK".equalsIgnoreCase(countryData.getName())) countryData.setName("Great Britain");
			Country country=CountryManager.getInstance().getCountryByName(countryData.getName());
			if (country!=null) countryData.setSymbol(country.getSymbol());
		}
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url=url;
	}

	public MovieData getMovieData()
	{
		return movieData;
	}

	public Set<Movie> getMovies()
	{
		return movies;
	}

	public Set<Language> getAllLanguages()
	{
		return LanguageManager.getInstance().getLanguages();
	}

	public Set<Country> getAllCountries()
	{
		return CountryManager.getInstance().getCountries();
	}

	public Collection<CreditType> getAllCreditTypes()
	{
		return CreditType.noCastValues();
	}
}
