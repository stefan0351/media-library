package com.kiwisoft.xp;

import org.xml.sax.Attributes;

import com.kiwisoft.media.show.*;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLTagHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 21.05.2004
 * Time: 15:06:27
 * To change this template use File | Settings | File Templates.
 */
public class LinkTagHandler implements XMLTagHandler
{
	public String startTag(XMLContext context, String uri, String localName, String rawName, Attributes attributes)
	{
		String showKey=attributes.getValue("show");
		String episodeKey=attributes.getValue("episode");
		if (showKey!=null)
		{
			if (episodeKey!=null)
			{
				Episode episode=ShowManager.getInstance().getEpisode(showKey, episodeKey);
				if (episode!=null)
				{
					EpisodeInfo info=episode.getDefaultInfo();
					if (info!=null) return "<a class=\"link\" href=\"/"+info.getPath()+"?episode="+episode.getId()+"\">";
				}
			}
			else
			{
				Show show=ShowManager.getInstance().getShow(showKey);
				if (show!=null)
				{
					return "<a class=\"link\" href=\""+show.getLink()+"\">";
				}
			}
		}
		return "";
	}

	public String endTag(XMLContext context, String uri, String localName, String rawName)
	{
		return "</a>";
	}
}
