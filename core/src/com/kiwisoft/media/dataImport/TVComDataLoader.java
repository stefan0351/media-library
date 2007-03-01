package com.kiwisoft.media.dataImport;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.gui.progress.ObservableRunnable;
import com.kiwisoft.utils.gui.progress.ProgressListener;
import com.kiwisoft.utils.gui.progress.ProgressSupport;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.media.show.Show;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 28.02.2007
 * Time: 21:00:08
 * To change this template use File | Settings | File Templates.
 */
public class TVComDataLoader implements ObservableRunnable
{
	private ProgressSupport progress;

	private Show show;
	private String baseUrl;
	private int startSeason;
	private int endSeason;

	public TVComDataLoader(Show show, String baseUrl, int startSeason, int endSeason)
	{
		this.show=show;
		this.baseUrl=baseUrl;
		this.startSeason=startSeason;
		this.endSeason=endSeason;
	}

	public String getName()
	{
		return "Lade Daten von TV.com";
	}

	public void setProgress(ProgressListener progress)
	{
		this.progress=new ProgressSupport(progress);
	}

	public void run()
	{
		try
		{
			for (int season=startSeason;season<=endSeason;season++)
			{
				loadSeason(baseUrl, season);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progress.error(e.getMessage());
		}
	}

	private void loadSeason(String baseUrl, int season) throws IOException, InterruptedException
	{
		String page=WebUtils.loadURL(baseUrl+"?season="+season);
//		FileUtils.saveToFile(page, new File("c:"+File.separator+"Incoming"+File.separator+"season"+season+".html"));

		// Search title
		int index1=page.indexOf("<h1>");
		int index2=page.indexOf("</h1>", index1);
		String showName=page.substring(index1+"<h1>".length(), index2);
		System.out.println("title = "+showName);

		// Search episode list
		index2=page.indexOf("<th>Episode</th>", index2);
		while (true)
		{
			index1=page.indexOf("<tr", index2);
			if (index1<0) break;
			index1=page.indexOf(">", index1);
			index2=page.indexOf("</tr>", index1);
			if (index2<0) break;
			String htmlRow=page.substring(index1+1, index2);
			List<String> values=extractCellValues(htmlRow);
			if (values.size()<4) break;

			String nameAndLink=values.get(1);
			XMLUtils.Tag startTag=XMLUtils.getNextTag(nameAndLink, 0, "a");
			XMLUtils.Tag endTag=XMLUtils.getNextTag(nameAndLink, startTag.end, "/a");

			EpisodeData data=new EpisodeData();
			data.setEpisodeNumber(values.get(0));
			data.setEpisodeName(nameAndLink.substring(startTag.end+1, endTag.start));
			data.setAirdate(values.get(2));
			data.setProductionCode(values.get(3));
			String episodeUrl=XMLUtils.getAttribute(startTag.text, "href");
			Thread.sleep(1000); // To avoid DOS on the TV.com server
			loadEpisode(episodeUrl, data);
		}
	}

	private void loadEpisode(String episodeUrl, EpisodeData data) throws IOException
	{
		String episodePage=WebUtils.loadURL(episodeUrl);
//		FileUtils.saveToFile(episodePage, new File("c:"+File.separator+"Incoming"+File.separator+"episode"+data.getEpisodeNumber()+".html"));

		int index1=episodePage.indexOf("<div id=\"episode-tabs\">");
		index1=episodePage.indexOf("<div id=\"main-col\">", index1);
		index1=episodePage.indexOf("<div>", index1);
		int index2=episodePage.indexOf("<div", index1+5);
		String content=episodePage.substring(index1+5, index2).trim();
		data.setContent(content);

		index1=episodePage.indexOf("<h1>Cast and Crew</h1>", index2);
		int index3=episodePage.indexOf("</table>", index1);
		while (index1>0 && index1<index3)
		{
			index1=episodePage.indexOf("<tr", index1);
			index2=episodePage.indexOf("<tr", index1+3);
			if (index2>index3) index2=index3;
			String htmlRow=episodePage.substring(index1, index2);
			List<String> values=extractCellValues(htmlRow);
			if (values.size()==2)
			{
				String creditName=values.get(0);
				String creditValue=values.get(1);
				if ("Writer:".equals(creditName) || "Director:".equals(creditName) || "Story:".equals(creditName))
				{
					creditValue=XMLUtils.removeTags(creditValue).trim();
					creditValue=XMLUtils.resolveEntities(creditValue);
					System.out.println(creditName+" = "+Arrays.asList(creditValue.split(",")));
				}
				else if ("Star:".equals(creditName) || "Recurring Role:".equals(creditName) || "Guest Star:".equals(creditName))
				{
					creditValue=XMLUtils.removeTags(creditValue).trim();
					List<CastData> cast=extractCast(creditValue);
					System.out.println(creditName+" = "+cast);
				}
				else System.err.println("Unknown credit: "+creditName+"="+creditValue);
			}
			index1=index2;
		}
	}

	private List<CastData> extractCast(String value)
	{
		List<CastData> castList=new ArrayList<CastData>();
		String[] castStrings=value.split(",&nbsp;");
		Pattern pattern=Pattern.compile("(.+) \\((.*)\\).*");
		for (int i=0; i<castStrings.length; i++)
		{
			String cast=castStrings[i];
			Matcher matcher=pattern.matcher(cast);
			if (matcher.matches()) castList.add(new CastData(matcher.group(1), matcher.group(2)));
			else System.err.println("Invalid cast pattern: "+cast);
		}
		return castList;
	}

	private List<String> extractCellValues(String htmlRow)
	{
		List<String> values=new ArrayList<String>(6);
		int index2=0;
		while (true)
		{
			int index1=htmlRow.indexOf("<td", index2);
			if (index1<0) break;
			index1=htmlRow.indexOf(">", index1);
			index2=htmlRow.indexOf("</td>", index1);
			if (index2<0) break;
			values.add(htmlRow.substring(index1+1, index2).trim());
		}
		return values;
	}

	private static class EpisodeData
	{
		private String episodeNumber;
		private String episodeName;
		private String airdate;
		private String productionCode;
		private String content;

		public void setEpisodeNumber(String episodeNumber)
		{
			this.episodeNumber=episodeNumber;
		}

		public void setEpisodeName(String episodeName)
		{
			this.episodeName=episodeName;
		}

		public void setAirdate(String airdate)
		{
			this.airdate=airdate;
		}

		public void setProductionCode(String productionCode)
		{
			this.productionCode=productionCode;
		}

		public String getEpisodeNumber()
		{
			return episodeNumber;
		}

		public void setContent(String content)
		{
			this.content=content;
		}
	}

	private static class CastData
	{
		private String actor;
		private String character;

		public CastData(String actor, String character)
		{
			this.actor=actor;
			this.character=character;
		}

		@Override
		public String toString()
		{
			return actor+" as "+character;
		}
	}
}
