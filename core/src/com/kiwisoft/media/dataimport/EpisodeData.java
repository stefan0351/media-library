package com.kiwisoft.media.dataimport;

import java.util.*;

/**
 * @author Stefan Stiller
*/
public class EpisodeData
{
	public static final String DETAILS_LINK="details";

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
	private List<EpisodeDataLoader.CrewData> writtenBy;
	private List<EpisodeDataLoader.CrewData> directedBy;
	private Map<String, String> links=new HashMap<String, String>();

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
		writtenBy=new ArrayList<EpisodeDataLoader.CrewData>();
		directedBy=new ArrayList<EpisodeDataLoader.CrewData>();
		mainCast=new ArrayList<EpisodeDataLoader.CastData>();
		recurringCast=new ArrayList<EpisodeDataLoader.CastData>();
		guestCast=new ArrayList<EpisodeDataLoader.CastData>();
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key=key;
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

	public List<EpisodeDataLoader.CrewData> getWrittenBy()
	{
		return writtenBy;
	}

	public List<EpisodeDataLoader.CrewData> getDirectedBy()
	{
		return directedBy;
	}

	public void addWrittenBy(EpisodeDataLoader.CrewData personData)
	{
		writtenBy.add(personData);
	}

	public void addDirectedBy(EpisodeDataLoader.CrewData person)
	{
		directedBy.add(person);
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

	public void setLink(String key, String link)
	{
		links.put(key, link);
	}

	public String getLink(String key)
	{
		return links.get(key);
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
