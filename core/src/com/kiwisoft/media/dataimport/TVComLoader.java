package com.kiwisoft.media.dataimport;

import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.xml.XMLUtils;
import static com.kiwisoft.utils.xml.XMLUtils.unescapeHtml;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
public abstract class TVComLoader extends EpisodeDataLoader
{
	private SimpleDateFormat airdateFormat;

	protected TVComLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		super(show, baseUrl, startSeason, endSeason, autoCreate);
		Matcher matcher=Pattern.compile(("(http://www.tv.com/.*/show/[0-9]+/).*")).matcher(baseUrl);
		if (matcher.matches()) setBaseUrl(matcher.group(1));
		airdateFormat=new SimpleDateFormat("M/d/yyyy");
	}

	public String getName()
	{
		return "Load Episodes from TV.com";
	}

	@Override
	protected List<EpisodeData> loadEpisodeList(int season) throws IOException
	{
		String page=loadUrl(getBaseUrl()+"episode.html?shv=list&season="+season);
        // Parse episode list
		int index1;
		int index2=page.indexOf("<div id=\"episode_listing\">", 0);
		int episodeIndex=1;
		List<EpisodeData> episodes=new ArrayList<EpisodeData>();
		while (true)
		{
			if (getProgress().isStoppedByUser()) return null;
			index1=page.indexOf("<tr class=\"episode\"", index2);
			if (index1<0) break;
			index1=page.indexOf(">", index1);
			index2=page.indexOf("</tr>", index1);
			if (index2<0) break;
			String htmlRow=page.substring(index1+1, index2);
			List<String> values=XMLUtils.extractCellValues(htmlRow);
			if (values.size()<4) break;

			String episodeKey=XMLUtils.removeTags(values.get(0));
			if (isNumber(episodeKey)) episodeKey=season+"."+episodeKey;
			else episodeKey=season+"."+episodeIndex;
			episodeIndex++;
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
			String title=convertHTML(nameAndLink.substring(startTag.end+1, endTag.start));
			EpisodeData data=new EpisodeData(episodeKey, title, airdate, code);
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

	@Override
	protected void loadDetails(EpisodeData episodeData) throws IOException
	{
		String page=loadUrl(episodeData.getEpisodeUrl());

		// Search summary
		int summaryStart=page.indexOf("<h3>Episode Summary</h3>");
		summaryStart=page.indexOf("</div>", summaryStart);
		summaryStart=page.indexOf("<p>", summaryStart);
		summaryStart=page.indexOf(">", summaryStart)+1;
		int summaryEnd=page.indexOf("</p>", summaryStart);
		String content=page.substring(summaryStart, summaryEnd).trim();
		content=ImportUtils.replaceHtmlFormatTags(content);
		Matcher matcher=Pattern.compile(" <a href=\"[^\"]*\">(Read full|Add a) recap &raquo;</a>").matcher(content);
		if (matcher.find()) content=content.substring(0, matcher.start());
		content=unescapeHtml(content).trim();
		episodeData.setEnglishSummary(content);

		// Search credits
		String baseUrl=episodeData.getEpisodeUrl();
		baseUrl=baseUrl.substring(0, baseUrl.lastIndexOf("/")+1);
		page=loadUrl(baseUrl+"cast.html");

		Pattern pattern=Pattern.compile("<li class=\"[^\"]*\"><div class=\"wrap\">"+
										"<div class=\"score_data\">Person Score<div class=\"score\">[0-9\\.]*</div><a href=\"[^\"]*\">[0-9]* Reviews?</a></div>"+
										"<div class=\"cast_data (?:no_thumb)?\">(?:<a class=\"thumb\" href=\"[^\"]*\" rel=\"nofollow\"><img src=\"http://image.com.com/tv/images/b.gif\" alt=\"Image of \" style=\"background:url\\([^\\)]*\\) no-repeat center;\" /></a>)?"+
										"<div class=\"personal\">" +
										"<h4 class=\"name\"><a href=\"(http://www.tv.com/[^\"]+/person/([0-9]+)/summary.html)\\?tag=cast;cast;([a-z_]+);name;[0-9]+\">([^<]+)</a></h4>" +
										"(?: <a class=\"photos_link\" href=\"[^\"]*\">\\(photos\\)</a>)?" +
										"</div>"+
										"<div class=\"role\">Role: (.*?)</div>" +
										"(?:<p class=\"intro\">(.*?)<a class=\"more_link\" href=\"[^\"]*\">Read More &raquo;</a></p>)?" +
										"</div></div></li>"
		, Pattern.DOTALL);
		matcher=pattern.matcher(page);
		int index=0;
		while (matcher.find(index))
		{
//			System.out.println("url="+matcher.group(1));
			String key=matcher.group(2);
			String type=matcher.group(3);
			String actor=convertHTML(matcher.group(4));
			String role=convertHTML(matcher.group(5));
//			System.out.println("description="+matcher.group(6));

			PersonData personData=new PersonData(key, actor);
			CastData castData=new CastData(personData, role);
			if ("star".equals(type)) episodeData.addMainCast(castData);
			else if ("recurring_role".equals(type)) episodeData.addRecurringCast(castData);
			else if ("guest_star".equals(type)) episodeData.addGuestCast(castData);
			else getProgress().error("Unknown cast type: "+type);
			index=matcher.end();
		}

		// Load directors/writers
		page=loadUrl(baseUrl+"cast.html?flag=6");
		matcher=pattern.matcher(page);
		index=0;
		while (matcher.find(index))
		{
//			System.out.println("url="+matcher.group(1));
			String key=matcher.group(2);
			String type=matcher.group(3);
			String person=convertHTML(matcher.group(4));
//			String role=convertHTML(matcher.group(5));
//			System.out.println("description="+matcher.group(6));

			PersonData personData=new PersonData(key, person);
			if ("writer".equals(type)) episodeData.addWrittenBy(personData);
			else if ("director".equals(type)) episodeData.addDirectedBy(personData);
			else if (!"crew".equals(type)) getProgress().error("Unknown cast type: "+type);
			index=matcher.end();
		}
	}
}
