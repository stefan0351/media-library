package com.kiwisoft.media.dataimport;

import com.kiwisoft.utils.PropertyChangeSource;

import java.util.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * @author Stefan Stiller
*/
public class EpisodeData implements PropertyChangeSource
{
	public static final String DETAILS_LINK="details";

	public static final String KEY="key";
	public static final String GERMAN_TITLE="germanTitle";
	public static final String TITLE="title";
	public static final String PRODUCTION_CODE="productionCode";

	private String key;
	private String title;
	private String germanTitle;
	private Date airdate;
	private String productionCode;
	private String englishSummary;
	private String germanSummary;
	private List<CastData> mainCast;
	private List<CastData> recurringCast;
	private List<CastData> guestCast;
	private List<CrewData> writtenBy;
	private List<CrewData> directedBy;
	private Map<String, String> links=new HashMap<String, String>();
	private boolean detailsLoaded;

	private PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);

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
		writtenBy=new ArrayList<CrewData>();
		directedBy=new ArrayList<CrewData>();
		mainCast=new ArrayList<CastData>();
		recurringCast=new ArrayList<CastData>();
		guestCast=new ArrayList<CastData>();
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		String oldKey=this.key;
		this.key=key;
		propertyChangeSupport.firePropertyChange(KEY, oldKey, this.key);
	}

	public void setGermanTitle(String germanTitle)
	{
		String oldTitle=this.germanTitle;
		this.germanTitle=germanTitle;
		propertyChangeSupport.firePropertyChange(GERMAN_TITLE, oldTitle, this.germanTitle);
	}

	public String getGermanTitle()
	{
		return germanTitle;
	}

	public void setTitle(String title)
	{
		String oldTitle=this.title;
		this.title=title;
		propertyChangeSupport.firePropertyChange(TITLE, oldTitle, this.title);
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

	public void setProductionCode(String productionCode)
	{
		String oldCode=this.productionCode;
		this.productionCode=productionCode;
		propertyChangeSupport.firePropertyChange(PRODUCTION_CODE, oldCode, this.productionCode);
	}

	public List<CastData> getMainCast()
	{
		return mainCast;
	}

	public List<CastData> getRecurringCast()
	{
		return recurringCast;
	}

	public List<CastData> getGuestCast()
	{
		return guestCast;
	}

	public List<CrewData> getWrittenBy()
	{
		return writtenBy;
	}

	public List<CrewData> getDirectedBy()
	{
		return directedBy;
	}

	public void addWrittenBy(CrewData personData)
	{
		writtenBy.add(personData);
	}

	public void addDirectedBy(CrewData person)
	{
		directedBy.add(person);
	}

	public void addMainCast(CastData cast)
	{
		mainCast.add(cast);
	}

	public void addRecurringCast(CastData cast)
	{
		recurringCast.add(cast);
	}

	public void addGuestCast(CastData cast)
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

	public void setDetailsLoaded(boolean detailsLoaded)
	{
		this.detailsLoaded=detailsLoaded;
	}

	public boolean isDetailsLoaded()
	{
		return detailsLoaded;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
