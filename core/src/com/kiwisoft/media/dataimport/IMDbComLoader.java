package com.kiwisoft.media.dataimport;

import com.kiwisoft.html.CssNodeFilter;
import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.html.PlainTextFilter;
import com.kiwisoft.media.person.CreditType;
import com.kiwisoft.utils.HttpConstants;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.xml.XMLUtils;
import org.apache.commons.lang.WordUtils;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Node;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
public class IMDbComLoader
{
	private String url;
	private Pattern nameLinkPattern;
	private String key;
	private Map<String, String> connectionProperties;

	public IMDbComLoader(String url, String key)
	{
		this.key=key;
		if (!url.endsWith("/")) url=url+"/";
		this.url=url;
		nameLinkPattern=Pattern.compile("/name/(nm[0-9]+)/");
		connectionProperties=new HashMap<String, String>();
		connectionProperties.put(HttpConstants.USER_AGENT, HttpConstants.AGENT_FIREFOX_3);
	}

	public MovieData load() throws Exception
	{
		String page=ImportUtils.loadUrl(url, connectionProperties);
		MovieData movieData=parseMainPage(page);
		movieData.setImdbKey(key);
		URL baseUrl=new URL(url);
		if (movieData.getCreditsLink()!=null)
		{
			page=ImportUtils.loadUrl(new URL(baseUrl, movieData.getCreditsLink()).toString(), connectionProperties);
			parseCreditsPage(page, movieData);
		}
		if (movieData.getPlotSynopsisLink()!=null)
		{
			page=ImportUtils.loadUrl(new URL(baseUrl, movieData.getPlotSynopsisLink()).toString(), connectionProperties);
			parseSynopsisPage(page, movieData);
		}
		if (StringUtils.isEmpty(movieData.getSummary()) && movieData.getPlotSummaryLink()!=null)
		{
			page=ImportUtils.loadUrl(new URL(baseUrl, movieData.getPlotSummaryLink()).toString(), connectionProperties);
			parseSummaryPage(page, movieData);
		}
		if (StringUtils.isEmpty(movieData.getSummary())) movieData.setSummary(movieData.getOutline());
		if (movieData.getReleaseInfoLink()!=null)
		{
			page=ImportUtils.loadUrl(new URL(baseUrl, movieData.getReleaseInfoLink()).toString(), connectionProperties);
			parseReleaseInfoPage(page, movieData);
		}
		return movieData;
	}

	private MovieData parseMainPage(String page) throws ParserException
	{
		MovieData movieData=new MovieData();

		Matcher matcher;
		Pattern pattern;

		Parser parser=new Parser();
		parser.setInputHTML(page);

		CompositeTag bodyTag=(CompositeTag) HtmlUtils.findFirst(parser, "body");

		CompositeTag linksTag=(CompositeTag) HtmlUtils.findFirst(bodyTag, "select#quicklinks_select");
		Tag optionTag=(Tag) HtmlUtils.findFirst(linksTag, new PlainTextFilter("full cast and crew"));
		if (optionTag!=null) movieData.setCreditsLink(optionTag.getAttribute("value"));
		optionTag=(Tag) HtmlUtils.findFirst(linksTag, new PlainTextFilter("plot summary"));
		if (optionTag!=null) movieData.setPlotSummaryLink(optionTag.getAttribute("value"));
		optionTag=(Tag) HtmlUtils.findFirst(linksTag, new PlainTextFilter("synopsis"));
		if (optionTag!=null) movieData.setPlotSynopsisLink(optionTag.getAttribute("value"));
		optionTag=(Tag) HtmlUtils.findFirst(linksTag, new PlainTextFilter("release dates"));
		if (optionTag!=null) movieData.setReleaseInfoLink(optionTag.getAttribute("value"));

		CompositeTag titleTag=(CompositeTag) HtmlUtils.findFirst(bodyTag, "h1.header");
		CompositeTag spanTag=(CompositeTag) HtmlUtils.findFirst(titleTag, "span");
		String title=page.substring(titleTag.getTagEnd(), spanTag.getTagBegin());
		title=StringUtils.trimQuotes(HtmlUtils.trimUnescape(title));
		movieData.setTitle(title);
		spanTag=(CompositeTag) HtmlUtils.findFirst(titleTag, "span.title-extra");
		if (spanTag!=null)
		{
			title=HtmlUtils.trimUnescape(spanTag.toPlainTextString());
			if (title.endsWith("(original title)"))
			{
				title=title.substring(0, title.length()-"(original title)".length()).trim();
				movieData.setTitle(title);
			}
		}


		CompositeTag yearTag=(CompositeTag) HtmlUtils.findFirst(titleTag, new LinkRegexFilter("/year/\\d{4}"));
		if (yearTag!=null)
		{
			int index="/year/".length();
			movieData.setYear(Integer.valueOf(yearTag.getAttribute("href").substring(index, index+4)));
		}

		Node plotTag=HtmlUtils.findFirst(bodyTag, new AndFilter(new TagNameFilter("h2"), new PlainTextFilter("Storyline")));
		if (plotTag!=null)
		{
			Node parentTag=plotTag.getParent();
			String html=parentTag.toHtml(true);
			int index1=html.indexOf("</h2>")+5;
			int index2=html.indexOf("<span", index1);
			if (index2>index1)
			{
				String outline=ImportUtils.toPreformattedText(html.substring(index1, index2).trim());
				while (outline.startsWith("[br/]")) outline=outline.substring(5);
				movieData.setOutline(outline);
			}
		}

		Node infoTag=HtmlUtils.findFirst(bodyTag, "div.infobar");
		if (infoTag!=null)
		{
			String value=infoTag.toHtml(true);
			pattern=Pattern.compile("\\b(\\d+) min\\b");
			matcher=pattern.matcher(value);
			if (matcher.find()) movieData.setRuntime(new Integer(matcher.group(1)));
		}

		Node countryTag=HtmlUtils.findFirst(bodyTag, new AndFilter(new CssNodeFilter("h4.inline"), new PlainTextFilter("Country:")));
		if (countryTag!=null)
		{
			countryTag=countryTag.getParent();
			NodeList linkTags=HtmlUtils.findAll((CompositeTag) countryTag, "a");
			for (NodeIterator it=linkTags.elements();it.hasMoreNodes();)
			{
				LinkTag linkTag=(LinkTag) it.nextNode();
				movieData.addCountry(new CountryData(linkTag.toPlainTextString().trim()));
			}
		}

		Node languageTag=HtmlUtils.findFirst(bodyTag, new AndFilter(new CssNodeFilter("h4.inline"), new PlainTextFilter("Language:")));
		if (languageTag!=null)
		{
			languageTag=languageTag.getParent();
			NodeList linkTags=HtmlUtils.findAll((CompositeTag) languageTag, "a");
			for (NodeIterator it=linkTags.elements();it.hasMoreNodes();)
			{
				LinkTag linkTag=(LinkTag) it.nextNode();
				movieData.addLanguage(new LanguageData(linkTag.toPlainTextString().trim()));
			}
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
		if (index>=0)
		{
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
	}

	private String getNameLink(String html)
	{
		Matcher keyMatcher=nameLinkPattern.matcher(XMLUtils.getAttribute(html, "href"));
		if (keyMatcher.matches()) return keyMatcher.group(1);
		return null;
	}

	private void parseSummaryPage(String page, MovieData movieData)
	{
		try
		{
			int index1=page.indexOf("<p class=\"plotpar\">");
			if (index1>=0)
			{
				index1=page.indexOf(">", index1)+1;
				int index2=page.indexOf("</p>", index1);
				if (index2>index1)
				{
					String summary=StringUtils.trimAll(XMLUtils.unescapeHtml(page.substring(index1, index2)));
					summary=XMLUtils.removeTag(summary, "a");
					summary=summary.replace("<", "[").replace(">", "]");
					movieData.setSummary(summary);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(page);
		}
	}

	private void parseSynopsisPage(String page, MovieData movieData)
	{
		int index1=page.indexOf("<div id=\"swiki.2.1\">");
		if (index1>=0)
		{
			index1=page.indexOf(">", index1)+1;
			int index2=page.indexOf("</div>", index1);
			String summary=StringUtils.trimAll(XMLUtils.unescapeHtml(page.substring(index1, index2)));
			summary=XMLUtils.removeTag(summary, "a");
			summary=summary.replace("<", "[").replace(">", "]");
			movieData.setSummary(summary);
		}
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
}
