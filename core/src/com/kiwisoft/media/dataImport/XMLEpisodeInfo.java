package com.kiwisoft.media.dataImport;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.kiwisoft.utils.xml.XMLAdapter;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLObject;
import com.kiwisoft.utils.xml.DefaultXMLObject;
import com.kiwisoft.utils.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 27.02.2007
 * Time: 20:57:58
 * To change this template use File | Settings | File Templates.
 */
public class XMLEpisodeInfo extends XMLAdapter
{
	private String show;
	private String episode;
	private String content;
	private Set cast=new HashSet();
	private Set directors=new HashSet();
	private Set writers=new HashSet();
	private String originalTitle;
	private String file;
	private Set subTitles=new LinkedHashSet();

	public XMLEpisodeInfo(XMLContext context, String name)
	{
		super(context, name);
		file=context.getFileName();
	}

	public String getFile()
	{
		return file;
	}

	public String getShow()
	{
		return show;
	}

	public String getEpisode()
	{
		if (subTitles.isEmpty()) return episode;
		else return episode+" ("+StringUtils.formatAsEnumeration(subTitles)+")";
	}

	public String getOriginalTitle()
	{
		return originalTitle;
	}

	public String getContent()
	{
		return content;
	}

	public Set getCast()
	{
		return cast;
	}

	public Set getDirectors()
	{
		return directors;
	}

	public Set getWriters()
	{
		return writers;
	}

	public boolean isCreditsAvailable()
	{
		return !writers.isEmpty() || !directors.isEmpty() || !cast.isEmpty();
	}

	public void addXMLElement(XMLContext context, XMLObject element)
	{
		if (element instanceof GermanEpisodeImport.CastInformation)
		{
			GermanEpisodeImport.CastInformation castInfo=(GermanEpisodeImport.CastInformation)element;
			if (castInfo.getCast()!=null)
				cast.addAll(castInfo.getCast());
			else
				cast.add(element);
		}
		else if (element instanceof DefaultXMLObject)
		{
			DefaultXMLObject xmlObject=(DefaultXMLObject)element;
			String name=xmlObject.getName();
			if ("Show".equalsIgnoreCase(name))
				show=xmlObject.getContent();
			else if ("Episode".equalsIgnoreCase(name))
				episode=xmlObject.getContent();
			else if ("Originaltitel".equalsIgnoreCase(name))
				originalTitle=xmlObject.getContent();
			else if ("Inhalt".equalsIgnoreCase(name))
				content=xmlObject.getContent();
			else if ("Drehbuch".equalsIgnoreCase(name))
				writers.add(xmlObject.getContent());
			else if ("Regie".equalsIgnoreCase(name))
				directors.add(xmlObject.getContent());
			else if ("Untertitel".equalsIgnoreCase(name))
				subTitles.add(xmlObject.getContent());
		}
	}

	public boolean equals(Object o)
	{
		if (this==o) return true;
		if (!(o instanceof XMLEpisodeInfo)) return false;

		final XMLEpisodeInfo episodeInformation=(XMLEpisodeInfo)o;

		if (cast!=null ? !cast.equals(episodeInformation.cast) : episodeInformation.cast!=null) return false;
		if (content!=null ? !content.equals(episodeInformation.content) : episodeInformation.content!=null) return false;
		if (directors!=null ? !directors.equals(episodeInformation.directors) : episodeInformation.directors!=null) return false;
		if (episode!=null ? !episode.equals(episodeInformation.episode) : episodeInformation.episode!=null) return false;
		if (show!=null ? !show.equals(episodeInformation.show) : episodeInformation.show!=null) return false;
		return !(writers!=null ? !writers.equals(episodeInformation.writers) : episodeInformation.writers!=null);
	}

	public int hashCode()
	{
		int result;
		result=(show!=null ? show.hashCode() : 0);
		result=29*result+(episode!=null ? episode.hashCode() : 0);
		result=29*result+(content!=null ? content.hashCode() : 0);
		result=29*result+(cast!=null ? cast.hashCode() : 0);
		result=29*result+(directors!=null ? directors.hashCode() : 0);
		result=29*result+(writers!=null ? writers.hashCode() : 0);
		return result;
	}
}
