package com.kiwisoft.media.dataImport;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.WebUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import com.kiwisoft.media.LanguageManager;
import com.kiwisoft.media.Language;
import com.kiwisoft.media.Country;
import com.kiwisoft.media.CountryManager;
import com.kiwisoft.media.person.CreditType;

/**
 * @author Stefan Stiller
 */
public class IMDbComLoader
{
	private String url;
	private Pattern nameLinkPattern;
	private String key;

	public IMDbComLoader(String url, String key)
	{
		this.key=key;
		if (!url.endsWith("/")) url=url+"/";
		this.url=url;
		nameLinkPattern=Pattern.compile("/name/(nm[0-9]+)/");
	}

	public MovieData load() throws Exception
	{
		String page=WebUtils.loadURL(url);
		MovieData movieData=parseMainPage(page);
		movieData.setImdbKey(key);
		if (movieData.getCreditsLink()!=null)
		{
			page=WebUtils.loadURL(url+movieData.getCreditsLink());
			parseCreditsPage(page, movieData);
		}
		if (movieData.getPlotSummaryLink()!=null)
		{
			page=WebUtils.loadURL(url+movieData.getPlotSummaryLink());
			parseSummaryPage(page, movieData);
		}
		if (movieData.getReleaseInfoLink()!=null)
		{
			page=WebUtils.loadURL(url+movieData.getReleaseInfoLink());
			parseReleaseInfoPage(page, movieData);
		}
		return movieData;
	}

	private MovieData parseMainPage(String page)
	{
		MovieData movieData=new MovieData();

		int index=0;
		Matcher matcher;
		Pattern pattern;

		pattern=Pattern.compile("<div class=\"(link|link empty)\"><i>-</i><a href=\"(\\w+)\">full cast and crew</a></div>");
		matcher=pattern.matcher(page);
		if (matcher.find(index))
		{
			if ("link".equals(matcher.group(1))) movieData.setCreditsLink(matcher.group(2));
			index=matcher.end();
		}

		pattern=Pattern.compile("<div class=\"(link|link empty)\"><i>-</i><a href=\"(\\w+)\">plot summary</a></div>");
		matcher=pattern.matcher(page);
		if (matcher.find(index))
		{
			if ("link".equals(matcher.group(1))) movieData.setPlotSummaryLink(matcher.group(2));
			index=matcher.end();
		}

		pattern=Pattern.compile("<div class=\"(link|link empty)\"><i>-</i><a href=\"(\\w+)\">release dates</a></div>");
		matcher=pattern.matcher(page);
		if (matcher.find(index))
		{
			if ("link".equals(matcher.group(1))) movieData.setReleaseInfoLink(matcher.group(2));
			index=matcher.end();
		}

		// Titel + Year
		pattern=Pattern.compile("<div id=\"tn15title\">\n"+
								"<h1>(.+) <span>\\(<a href=\"/Sections/Years/\\d{4}\">(\\d{4})</a>(/\\w+)?\\)( \\(TV\\)| \\(mini\\)| \\(V\\))?</span></h1>\n"+
								"</div>");
		matcher=pattern.matcher(page);
		if (matcher.find(index))
		{
			String title=XMLUtils.unescapeHtml(matcher.group(1)).trim();
			if (title.startsWith("\"") && title.endsWith("\"")) title=title.substring(1, title.length()-1);
			movieData.setTitle(title);
			movieData.setYear(Integer.parseInt(matcher.group(2)));
		}

		index=page.indexOf("Additional Details");
		int sectionEnd=page.indexOf("<hr", index);
		while (true)
		{
			index=page.indexOf("<div class=\"info\">", index);
			if (index<0 || index>sectionEnd) break;
			index=page.indexOf("<h5>", index);
			int index2=page.indexOf("</h5>", index);
			String type=page.substring(index+4, index2);
			String value=page.substring(index2+5, page.indexOf("</div>", index2)).trim();
			if ("Runtime:".equals(type))
			{
				value=StringUtils.splitAndTrim(value, "/")[0];
				pattern=Pattern.compile("\\w+:(\\d+) min");
				matcher=pattern.matcher(value);
				if (matcher.matches()) movieData.setRuntime(new Integer(matcher.group(1)));
				else
				{
					pattern=Pattern.compile("(\\d+) min");
					matcher=pattern.matcher(value);
					if (matcher.matches()) movieData.setRuntime(new Integer(matcher.group(1)));
				}
			}
			else if ("Country:".equals(type))
			{
				value=XMLUtils.removeTags(XMLUtils.unescapeHtml(value)).trim();
				for (String country : value.split("/")) movieData.addCountry(getCountry(country.trim()));
			}
			else if ("Language:".equals(type))
			{
				value=XMLUtils.removeTags(XMLUtils.unescapeHtml(value)).trim();
				for (String language : value.split("/")) movieData.addLanguage(getLanguage(language.trim()));
			}
			index=index2;
		}

		return movieData;
	}

	private void parseCreditsPage(String page, MovieData movieData)
	{
		int index=page.indexOf("<h1><small>Full cast and crew for");

		Pattern pattern=Pattern.compile("<h5><a class=\"glossary\" name=\"([^<]+)\" href=\"/Glossary/.#[^<]+\">([^<]+)</a>");
		Matcher matcher=pattern.matcher(page);
		while (matcher.find(index))
		{
			String typeName=matcher.group(2);
			CreditType type=null;
			if ("Directed by".equals(typeName)) type=CreditType.DIRECTOR;
			else if ("Writing credits".equals(typeName)) type=CreditType.WRITER;
			else if ("Produced by".equals(typeName)) type=CreditType.PRODUCER;
			else if ("Original Music by".equals(typeName)) type=CreditType.COMPOSER;
			else if ("Cinematography by".equals(typeName)) type=CreditType.CINEMATOGRAPHER;
			else if ("Film Editing by".equals(typeName)) type=CreditType.EDITOR;
			else if ("Art Direction by".equals(typeName)) type=CreditType.ART_DIRECTOR;
			if (type!=null)
			{
				index=matcher.end();
				int tableEnd=page.indexOf("</table>", index);
				while (true)
				{
					index=page.indexOf("<tr", index);
					if (index<0 || index>tableEnd) break;
					int index2=page.indexOf("</tr>", index);
					String htmlRow=page.substring(index, index2);
					List<String> row=XMLUtils.extractCellValues(htmlRow);
					String name=XMLUtils.unescapeHtml(row.get(0));
					if (!StringUtils.isEmpty(name))
					{
						String imdbKey=getNameLink(name);
						name=XMLUtils.removeTags(name).trim();
						String subType=null;
						if (row.size()>2)
						{
							subType=XMLUtils.removeTags(XMLUtils.unescapeHtml(row.get(2))).trim();
							if (subType.endsWith(" &")) subType=subType.substring(0, subType.length()-2);
							if (subType.endsWith(" and")) subType=subType.substring(0, subType.length()-4);
							if (subType.startsWith("(") && subType.endsWith(")")) subType=subType.substring(1, subType.length()-1);
							subType=WordUtils.capitalize(subType);
						}
						movieData.addCrew(new CrewData(name, type, subType, imdbKey));
					}
					index=index2;
				}
			}
			index=matcher.end();
		}

		index=page.indexOf("<table class=\"cast\">");
		int tableEnd=page.indexOf("</table>", index);
		int creditOrder=1;
		while (true)
		{
			index=page.indexOf("<tr", index);
			if (index<0 || index>tableEnd) break;
			int index2=page.indexOf("</tr>", index);
			if (index2<0 || index>tableEnd) break;
			String htmlRow=page.substring(index, index2);
			index=index2;
			List<String> row=XMLUtils.extractCellValues(htmlRow);
			if ("<small>rest of cast listed alphabetically:</small>".equals(row.get(0))) continue;
			String actor=XMLUtils.unescapeHtml(row.get(1));
			String imdbKey=getNameLink(actor);
			actor=XMLUtils.removeTags(actor).trim();
			String role=XMLUtils.removeTags(XMLUtils.unescapeHtml(row.get(3))).trim();
			if (!"Extra".equals(role) && !role.startsWith("Extra (as"))
			{
				movieData.addCast(new CastData(actor, role, creditOrder++, imdbKey));
			}
		}

	}

	private String getNameLink(String html)
	{
		Matcher keyMatcher=nameLinkPattern.matcher(XMLUtils.getAttribute(html, "href"));
		if (keyMatcher.matches()) return keyMatcher.group(1);
		return null;
	}

	private void parseSummaryPage(String page, MovieData movieData)
	{
		int index1=page.indexOf("<p class=\"plotpar\">");
		index1=page.indexOf(">", index1)+1;
		int index2=page.indexOf("</p>");
		String summary=StringUtils.trimString(XMLUtils.unescapeHtml(page.substring(index1, index2)));
		summary=XMLUtils.removeTag(summary, "a");
		summary=summary.replace("<", "[").replace(">", "]");
		movieData.setSummary(summary);
	}

	private void parseReleaseInfoPage(String page, MovieData movieData)
	{
		int index1=page.indexOf("<a name=\"akas\">");
		int tableEnd=page.indexOf("</table>", index1);
		while (true)
		{
			index1=page.indexOf("<tr", index1);
			if (index1<0 || index1>tableEnd) break;
			int index2=page.indexOf("</tr>", index1);
			String htmlRow=page.substring(index1, index2);
			List<String> row=XMLUtils.extractCellValues(htmlRow);
			if (row.size()>1)
			{
				String country=row.get(1);
				if (country.contains("Germany")) movieData.setGermanTitle(XMLUtils.removeTags(XMLUtils.unescapeHtml(row.get(0))).trim());
			}
			index1=index2;
		}
	}

	private Language getLanguage(String name)
	{
		ResourceBundle bundle=ResourceBundle.getBundle(IMDbComLoader.class.getName());
		String isoCode=bundle.getString("language."+name);
		Language language=LanguageManager.getInstance().getLanguageBySymbol(isoCode);
		if (language==null) throw new RuntimeException("Language "+name+" not found");
		return language;
	}

	private Country getCountry(String name)
	{
		ResourceBundle bundle=ResourceBundle.getBundle(IMDbComLoader.class.getName());
		String isoCode=bundle.getString("country."+name);
		Country country=CountryManager.getInstance().getCountryBySymbol(isoCode);
		if (country==null) throw new RuntimeException("Country "+name+" not found");
		return country;
	}
}
