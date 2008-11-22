package com.kiwisoft.media.dataimport;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Stefan Stiller
*/
public class EpisodeData
{
	private String key;
	private String title;
	private String germanTitle;
	private Date airdate;
	private String productionCode;
	private String englishSummary;
	private String germanSummary;
	private List<EpisodeDataLoader.CastData> mainCast;
	private List<EpisodeDataLoader.CastData> recurringCast;
	private List<EpisodeDataLoader.CastData> guestCast;
	private List<EpisodeDataLoader.PersonData> writtenBy;
	private List<EpisodeDataLoader.PersonData> directedBy;
	private List<EpisodeDataLoader.PersonData> storyBy;
	private String episodeUrl;

	public EpisodeData(String episodeKey, String episodeName)
	{
		this(episodeKey, episodeName, null, null);
	}

	public EpisodeData(String episodeKey, String episodeName, Date airdate, String productionCode)
	{
		this.key=episodeKey;
		this.title=episodeName;
		this.airdate=airdate;
		this.productionCode=productionCode;
		writtenBy=new ArrayList<EpisodeDataLoader.PersonData>();
		directedBy=new ArrayList<EpisodeDataLoader.PersonData>();
		storyBy=new ArrayList<EpisodeDataLoader.PersonData>();
		mainCast=new ArrayList<EpisodeDataLoader.CastData>();
		recurringCast=new ArrayList<EpisodeDataLoader.CastData>();
		guestCast=new ArrayList<EpisodeDataLoader.CastData>();
	}

	public String getKey()
	{
		return key;
	}

	public void setGermanTitle(String germanTitle)
	{
		this.germanTitle=germanTitle;
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public String getTitle()
	{
		return title;
	}

	public void setEnglishSummary(String englishSummary)
	{
		this.englishSummary=englishSummary;
	}

	public String getEnglishSummary()
	{
		return englishSummary;
	}

	public Date getFirstAirdate()
	{
		return airdate;
	}

	public String getProductionCode()
	{
		return productionCode;
	}

	public List<EpisodeDataLoader.CastData> getMainCast()
	{
		return mainCast;
	}

	public List<EpisodeDataLoader.CastData> getRecurringCast()
	{
		return recurringCast;
	}

	public List<EpisodeDataLoader.CastData> getGuestCast()
	{
		return guestCast;
	}

	public List<EpisodeDataLoader.PersonData> getWrittenBy()
	{
		return writtenBy;
	}

	public List<EpisodeDataLoader.PersonData> getDirectedBy()
	{
		return directedBy;
	}

	public List<EpisodeDataLoader.PersonData> getStoryBy()
	{
		return storyBy;
	}

	public void addWrittenBy(EpisodeDataLoader.PersonData personData)
	{
		writtenBy.add(personData);
	}

	public void addDirectedBy(EpisodeDataLoader.PersonData person)
	{
		directedBy.add(person);
	}

	public void addStoryBy(EpisodeDataLoader.PersonData person)
	{
		storyBy.add(person);
	}

	public void addMainCast(EpisodeDataLoader.CastData cast)
	{
		mainCast.add(cast);
	}

	public void addRecurringCast(EpisodeDataLoader.CastData cast)
	{
		recurringCast.add(cast);
	}

	public void addGuestCast(EpisodeDataLoader.CastData cast)
	{
		guestCast.add(cast);
	}

	public void setEpisodeUrl(String episodeUrl)
	{
		this.episodeUrl=episodeUrl;
	}

	public String getEpisodeUrl()
	{
		return episodeUrl;
	}

	public String getGermanSummary()
	{
		return germanSummary;
	}

	public void setGermanSummary(String germanSummary)
	{
		this.germanSummary=germanSummary;
	}

	public void setFirstAirdate(Date date)
	{
		this.airdate=date;
	}
}
