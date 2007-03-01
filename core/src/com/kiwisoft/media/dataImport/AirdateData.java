package com.kiwisoft.media.dataImport;

import java.util.Set;
import java.util.HashSet;

import com.kiwisoft.utils.xml.XMLAdapter;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.media.show.Episode;
import com.kiwisoft.media.show.ShowManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class AirdateData extends XMLAdapter
{
	private String showName;
	private String episodeName;
	private Set<AiringData> airings=new HashSet<AiringData>();
	private DataSource dataSource;
	private String event;
	private Show show;
	private Episode episode;

	public AirdateData(XMLContext context, String name)
	{
		super(context, name);
	}

	public Show getShow()
	{
		if (show==null) show=ShowManager.getInstance().getShowByName(showName);
		return show;
	}

	public String getShowName()
	{
		return showName;
	}

	public String getEpisodeName()
	{
		return episodeName;
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public Set<AiringData> getAirings()
	{
		return airings;
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof AiringData)
		{
			AiringData airing=(AiringData)element;
			airings.add(airing);
		}
		else if (element instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlObject=(DefaultXMLObject)element;
			if ("Show".equalsIgnoreCase(xmlObject.getName()))
				showName=xmlObject.getContent();
			else if ("Episode".equalsIgnoreCase(xmlObject.getName()))
				episodeName=xmlObject.getContent();
			else if ("DataSource".equalsIgnoreCase(xmlObject.getName()))
				dataSource=DataSource.get(xmlObject.getContent());
		}
	}

	public String toString()
	{
		return "Show="+showName+"; Episode="+episodeName+"; Termine="+airings;
	}

	public void setEvent(String event)
	{
		this.event=event;
}

	public String getEvent()
	{
		return event;
}

	public Episode getEpisode()
	{
		if (episode==null) episode=ShowManager.getInstance().getEpisodeByName(show, episodeName);
		return episode;
}
}
