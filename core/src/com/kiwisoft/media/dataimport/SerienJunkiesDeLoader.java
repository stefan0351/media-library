package com.kiwisoft.media.dataimport;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public abstract class SerienJunkiesDeLoader extends EpisodeDataLoader
{
	public SimpleDateFormat airdateFormat;

	protected SerienJunkiesDeLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		super(show, baseUrl, startSeason, endSeason, autoCreate);
		Matcher matcher=Pattern.compile("(http://www.serienjunkies.de/[^/]+)/.*").matcher(baseUrl);
		if (matcher.matches()) setBaseUrl(matcher.group(1));
		airdateFormat=new SimpleDateFormat("dd.MM.yy");
		airdateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
	}

	public String getName()
	{
		return "Load Episodes from SerienJunkies.de";
	}

	@Override
	protected List<EpisodeData> loadEpisodeList(int season) throws IOException
	{
		String page=loadUrl(getBaseUrl()+"/season"+season+".html");

		// Parse episode list
		int listStart=page.indexOf("<table class=\"eplist\">");
		int listEnd=page.indexOf("</table>", listStart);
		List<EpisodeData> episodes=new ArrayList<EpisodeData>();
		if (listStart>0 && listEnd>listStart)
		{
			int episodeEnd=listStart;
			while (true)
			{
				if (getProgress().isStoppedByUser()) return null;

				int episodeStart=page.indexOf("<tr>", episodeEnd);
				if (episodeStart==-1 || episodeStart>listEnd) break;
				episodeEnd=page.indexOf("</tr>", episodeStart);
				if (episodeEnd<episodeStart) break;
				String episodeRow=page.substring(episodeStart, episodeEnd);
				List<String> episodeCells=XMLUtils.extractCellValues(episodeRow);

				String key=season+"."+episodeCells.get(0).trim();
				String title=convertHTML(episodeCells.get(2));
				EpisodeData episodeData=new EpisodeData(key, title);
				episodeData.setGermanTitle(convertHTML(episodeCells.get(3)));

				String airdateText=convertHTML(episodeCells.get(1));
				if (!StringUtils.isEmpty(airdateText))
				{
					try
					{
						episodeData.setFirstAirdate(airdateFormat.parse(airdateText));
					}
					catch (ParseException e)
					{
						getProgress().error(e);
					}
				}

				XMLUtils.Tag linkTag=XMLUtils.getNextTag(episodeCells.get(6), 0, "a");
				if (linkTag!=null)
				{
					String href=XMLUtils.getAttribute(linkTag.text, "href");
					episodeData.setEpisodeUrl(new URL(new URL(getBaseUrl()), href).toString());
				}

				episodes.add(episodeData);
			}
		}
		return episodes;
	}

	@Override
	protected void loadDetails(EpisodeData data) throws IOException
	{
		String page=loadUrl(data.getEpisodeUrl());

		int tableStart=page.indexOf("<table id=\"epdetails\">");
		if (tableStart<0) return;
		int tableEnd=page.indexOf("</table>", tableStart);
		if (tableEnd<tableStart) return;

		int summaryStart=page.indexOf("<td colspan=\"2\">", tableStart);
		if (summaryStart<0 || summaryStart>tableEnd) return;
		summaryStart=page.indexOf(">", summaryStart)+1;
		int summaryEnd=page.indexOf("</td>", summaryStart);
		String summary=page.substring(summaryStart, summaryEnd);
		summary=summary.replaceAll("<em>Exklusive Episodenbeschreibung von .* f\u00fcr Serienjunkies.de:</em><br />", "");
		summary=XMLUtils.removeTag(summary, "img");
		StringBuilder preformattedText=new StringBuilder();
		boolean lineBreak=false;
		String[] lines=summary.split("<p[^>]*>|</p\\w*>");
		for (String line : lines)
		{
			if (StringUtils.isEmpty(line))
			{
				if (preformattedText.length()>0) lineBreak=true;
			}
			else
			{
				if (lineBreak) preformattedText.append("[br/]\n[br/]\n");
				lineBreak=false;
				preformattedText.append(convertHTML(line));
			}
		}
		data.setGermanSummary(preformattedText.toString());
	}
}
