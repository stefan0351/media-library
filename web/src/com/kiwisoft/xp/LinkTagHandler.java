package com.kiwisoft.xp;

import javax.servlet.http.HttpServletRequest;

import org.xml.sax.Attributes;

import com.kiwisoft.media.show.*;
import com.kiwisoft.media.Navigation;
import com.kiwisoft.utils.xml.XMLContext;
import com.kiwisoft.utils.xml.XMLTagHandler;

/**
 * @author Stefan Stiller
 */
public class LinkTagHandler implements XMLTagHandler
{
	public String startTag(XMLContext context, String name, Attributes attributes)
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
					return "<a class=\"link\" href=\""+Navigation.getLink((HttpServletRequest)context.getAttribute("request"), episode)+"\">";
				}
			}
			else
			{
				Show show=ShowManager.getInstance().getShow(showKey);
				if (show!=null)
				{
					return "<a class=\"link\" href=\""+Navigation.getLink((HttpServletRequest)context.getAttribute("request"), show)+"\">";
				}
			}
		}
		return "";
	}

	public String endTag(XMLContext context, String name)
	{
		return "</a>";
	}
}
