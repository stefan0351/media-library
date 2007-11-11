package com.kiwisoft.media.dataImport;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.kiwisoft.media.Country;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.movie.Movie;

/**
 * @author Stefan Stiller
 */
public class MovieData
{
	private String plotSummaryLink;
	private String releaseInfoLink;
	private String plotSynopsisLink;
	private String creditsLink;

	private String title;
	private String germanTitle;
	private Integer year;
	private String summary;
	private List<CrewData> crew=new ArrayList<CrewData>();
	private List<CastData> cast=new ArrayList<CastData>();
	private Integer runtime;
	private Set<Country> countries=new HashSet<Country>();
	private Set<Language> languages=new HashSet<Language>();
	private Movie movie;
	private String imdbKey;
	private String outline;

	public void addCrew(CrewData crewData)
	{
		getCrew().add(crewData);
	}

	public void addCast(CastData castData)
	{
		getCast().add(castData);
	}

	public void addCountry(Country country)
	{
		getCountries().add(country);
	}

	public void addLanguage(Language language)
	{
		getLanguages().add(language);
	}

	public String getTitle()
	{
		return title;
	}

	public String getPlotSummaryLink()
	{
		return plotSummaryLink;
	}

	public void setPlotSummaryLink(String plotSummaryLink)
	{
		this.plotSummaryLink=plotSummaryLink;
	}

	public String getReleaseInfoLink()
	{
		return releaseInfoLink;
	}

	public void setReleaseInfoLink(String releaseInfoLink)
	{
		this.releaseInfoLink=releaseInfoLink;
	}

	public String getCreditsLink()
	{
		return creditsLink;
	}

	public void setCreditsLink(String creditsLink)
	{
		this.creditsLink=creditsLink;
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	public Integer getYear()
	{
		return year;
	}

	public void setYear(Integer year)
	{
		this.year=year;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String summary)
	{
		this.summary=summary;
	}

	public Integer getRuntime()
	{
		return runtime;
	}

	public void setRuntime(Integer runtime)
	{
		this.runtime=runtime;
	}

	public List<CrewData> getCrew()
	{
		return crew;
	}

	public List<CastData> getCast()
	{
		return cast;
	}

	public Set<Country> getCountries()
	{
		return countries;
	}

	public Set<Language> getLanguages()
	{
		return languages;
	}

	public void setGermanTitle(String germanTitle)
	{
		this.germanTitle=germanTitle;
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setMovie(Movie movie)
	{
		this.movie=movie;
	}

	public Movie getMovie()
	{
		return movie;
	}

	public void setImdbKey(String imdbKey)
	{
		this.imdbKey=imdbKey;
	}

	public String getImdbKey()
	{
		return imdbKey;
	}

	@Override
	public String toString()
	{
		StringBuilder buffer=new StringBuilder("MovieData{");
		buffer.append("\n\ttitle=").append(title);
		buffer.append("\n\tgermanTitle=").append(germanTitle);
		buffer.append("\n\tlanguages=").append(languages);
		buffer.append("\n\tcountries=").append(countries);
		buffer.append("\n\tyear=").append(year);
		buffer.append("\n\truntime=").append(runtime);
		buffer.append("\n\timdbKey=").append(imdbKey);
		buffer.append("\n\tplotSummaryLink=").append(plotSummaryLink);
		buffer.append("\n\tplotSynopsisLink=").append(plotSynopsisLink);
		buffer.append("\n\tcreditsLink=").append(creditsLink);
		buffer.append("\n\treleaseInfoLink=").append(releaseInfoLink);
		buffer.append("\n\tsummary=").append(summary);
		buffer.append("}");
		return buffer.toString();
	}

	public void setPlotSynopsisLink(String plotSynopsisLink)
	{
		this.plotSynopsisLink=plotSynopsisLink;
	}

	public String getPlotSynopsisLink()
	{
		return plotSynopsisLink;
	}

	public void setOutline(String outline)
	{
		this.outline=outline;
	}

	public String getOutline()
	{
		return outline;
	}
}
