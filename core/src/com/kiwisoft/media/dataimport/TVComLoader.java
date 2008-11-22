package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import static com.kiwisoft.utils.xml.XMLUtils.unescapeHtml;

/**
 * @author Stefan Stiller
 */
public abstract class TVComLoader extends EpisodeDataLoader
{
	private SimpleDateFormat airdateFormat;
	private Pattern nameLinkPattern;

	protected TVComLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		super(show, baseUrl, startSeason, endSeason, autoCreate);
		airdateFormat=new SimpleDateFormat("M/d/yyyy");
		nameLinkPattern=Pattern.compile("http://www.tv.com/.*/person/([0-9]+)/summary.html");
	}

	public String getName()
	{
		return "Load Episodes from TV.com";
	}

	protected List<EpisodeData> loadEpisodeList(int season) throws IOException
	{
		String page=loadUrl(getBaseUrl()+"?season="+season);

		// Parse episode list
		int index1;
		int index2=page.indexOf("<th class=\"ep_title\"><div>episode</div></th>", 0);
		int episodeIndex=1;
		List<EpisodeData> episodes=new ArrayList<EpisodeData>();
		while (true)
		{
			if (getProgress().isStoppedByUser()) return null;
			index1=page.indexOf("<tr", index2);
			if (index1<0) break;
			index1=page.indexOf(">", index1);
			index2=page.indexOf("</tr>", index1);
			if (index2<0) break;
			String htmlRow=page.substring(index1+1, index2);
			List<String> values=XMLUtils.extractCellValues(htmlRow);
			if (values.size()<4) break;

			String episodeKey=XMLUtils.removeTags(values.get(0));
			if (isNumber(episodeKey)) episodeKey=season+"."+(episodeIndex++);
			String nameAndLink=values.get(1);
			XMLUtils.Tag startTag=XMLUtils.getNextTag(nameAndLink, 0, "a");
			XMLUtils.Tag endTag=XMLUtils.getNextTag(nameAndLink, startTag.end, "/a");
			Date airdate=null;
			try
			{
				airdate=airdateFormat.parse(XMLUtils.removeTags(values.get(2)));
				if (airdate.getTime()<0L) airdate=null;
			}
			catch (ParseException e)
			{
				e.printStackTrace();
				getProgress().error(e.getMessage());
			}

			String code=XMLUtils.removeTags(values.get(3));
			EpisodeData data=new EpisodeData(episodeKey, nameAndLink.substring(startTag.end+1, endTag.start), airdate, code);
			data.setEpisodeUrl(XMLUtils.getAttribute(startTag.text, "href"));

			episodes.add(data);
		}
		return episodes;
	}

	private boolean isNumber(String text)
	{
		try
		{
			Integer.parseInt(text);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	protected void loadDetails(EpisodeData episodeData) throws IOException
	{
		String episodePage=loadUrl(episodeData.getEpisodeUrl());

		// Search summary
		int summaryStart=episodePage.indexOf("<h3 class=\"title\">EPISODE OVERVIEW</h3>");
		summaryStart=episodePage.indexOf("<div class=\"details\">", summaryStart);
		summaryStart=episodePage.indexOf("<p class=\"deck\">", summaryStart);
		summaryStart=episodePage.indexOf(">", summaryStart)+1;
		int summaryEnd=episodePage.indexOf("</p>", summaryStart);
		String content=episodePage.substring(summaryStart+5, summaryEnd).trim();
		content=ImportUtils.replaceHtmlFormatTags(content);
		Matcher matcher=Pattern.compile(" <a href=\"[^\"]*\">(Read full|Add a) recap &raquo;</a>").matcher(content);
		if (matcher.find()) content=content.substring(0, matcher.start());
		content=unescapeHtml(content).trim();
		episodeData.setEnglishSummary(content);

		// Search credits
		int creditStart=episodePage.indexOf("<h3 class=\"title\">CAST AND CREW</h3>");
		creditStart=episodePage.indexOf(" <div class=\"info\">", creditStart);
		int creditEnd=episodePage.indexOf("</div", creditStart);
		if (creditStart>0 && creditEnd>creditStart)
		{
			int creditIndex=creditStart;
			while (true)
			{
				int itemStart=episodePage.indexOf("<dl", creditIndex);
				int itemEnd=episodePage.indexOf("</dl>", itemStart);
				if (itemStart>0 && itemEnd>itemStart && itemStart<creditEnd)
				{
					String item=episodePage.substring(itemStart, itemEnd);
					matcher=Pattern.compile("<dt>(.+):</dt>").matcher(item);
					if (matcher.find())
					{
						String creditName=matcher.group(1);
						int personIndex=matcher.end();
						while (true)
						{
							int personStart=item.indexOf("<dd>", personIndex);
							int personEnd=item.indexOf("</dd>", personStart);
							if (personStart>=0 && personEnd>personStart)
							{
								String personText=item.substring(personStart+4, personEnd).trim();

								if ("Writers".equals(creditName) || "Director".equals(creditName))
								{
									String[] personStrings=personText.split("</a>");
									for (String personString : personStrings)
									{
										String key=getPersonKey(personString);
										String name=convertHTML(personString);
										if ("Writers".equals(creditName)) episodeData.addWrittenBy(new PersonData(key, name));
										else if ("Director".equals(creditName)) episodeData.addDirectedBy(new PersonData(key, name));
									}
								}
								else if ("Stars".equals(creditName) || "Recurring Role".equals(creditName) || "Guest Star".equals(creditName))
								{
									int sep=personText.indexOf("</a>");
									if (sep==-1)
									{
										if (personText.endsWith(")"))
										{
											sep=StringUtils.findMatchingBrace(personText, personText.length()-1);
										}
									}
									String name=null;
									String key=null;
									String character;
									if (sep>0)
									{
										name=personText.substring(0, sep);
										key=getPersonKey(name);
										name=convertHTML(name);
										character=convertHTML(personText.substring(sep));
										if (character.startsWith("(") && character.endsWith(")"))
										{
											character=character.substring(1, character.length()-1);
										}
									}
									else
									{
										character=convertHTML(personText);
									}
									PersonData person=null;
									if (!StringUtils.isEmpty(name)) person=new PersonData(key, name);
									CastData castData=new CastData(person, character);
									if ("Stars".equals(creditName)) episodeData.addMainCast(castData);
									else if ("Recurring Role".equals(creditName)) episodeData.addRecurringCast(castData);
									else if ("Guest Star".equals(creditName)) episodeData.addGuestCast(castData);
								}
								else
								{
									getProgress().warning("Unknown credit: "+creditName);
									break;
								}
							}
							else break;
							personIndex=personEnd;
						}
					}

				}
				else break;
				creditIndex=itemEnd;
			}
		}
	}

	private String getPersonKey(String html)
	{
		Matcher keyMatcher=nameLinkPattern.matcher(XMLUtils.getAttribute(html, "href"));
		if (keyMatcher.find()) return keyMatcher.group(1);
		return null;
	}
}
