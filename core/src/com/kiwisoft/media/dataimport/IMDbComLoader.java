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
import org.htmlparser.tags.*;
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

	public static IMDbComLoader create(String url)
	{
		Matcher matcher=Pattern.compile("http://(?:german|www).imdb.(?:com|de)/title/(\\w+)\\b.*").matcher(url);
		if (matcher.matches())
		{
			url="http://www.imdb.com/title/"+matcher.group(1)+"/";
			return new IMDbComLoader(url, matcher.group(1));
		}
		return null;
	}

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

	private void parseCreditsPage(String page, MovieData movieData) throws ParserException
	{
		Parser parser=new Parser();
		parser.setInputHTML(page);

		Node bodyNode=HtmlUtils.findFirst(parser, "body");

		NodeList glossaryNodes=HtmlUtils.findAll((CompositeTag) bodyNode, "a.glossary");
		for (NodeIterator it=glossaryNodes.elements();it.hasMoreNodes();)
		{
			LinkTag linkTag=(LinkTag) it.nextNode();
			String linkName=linkTag.getAttribute("name");
			CreditType type=null;
			if ("directors".equals(linkName)) type=CreditType.DIRECTOR;
			else if ("producers".equals(linkName)) type=CreditType.PRODUCER;
			else if ("writers".equals(linkName)) type=CreditType.WRITER;
			else if ("music_original".equals(linkName)) type=CreditType.COMPOSER;
			else if ("cinematographers".equals(linkName)) type=CreditType.CINEMATOGRAPHER;
			else if ("editors".equals(linkName)) type=CreditType.EDITOR;
			else if ("art_directors".equals(linkName)) type=CreditType.ART_DIRECTOR;
			if (type!=null)
			{
				TableTag tableTag=(TableTag) HtmlUtils.findAncestor(linkTag, "table");
				for (TableRow tableRow : tableTag.getRows())
				{
					TableColumn[] columns=tableRow.getColumns();
					if (columns.length==1) continue; // Heading

					CrewData crewData=new CrewData();
					crewData.setType(type);
					crewData.setName(HtmlUtils.trimUnescape(columns[0].toPlainTextString()));
					if (!StringUtils.isEmpty(crewData.getName()))
					{
						LinkTag personLink=(LinkTag) HtmlUtils.findFirst(columns[0], "a");
						if (personLink!=null) crewData.setKey(extractKeyFromLink(personLink));
						if (columns.length>2)
						{
							String subType=HtmlUtils.trimUnescape(columns[2].toPlainTextString());
							if (subType.endsWith(" &")) subType=subType.substring(0, subType.length()-2);
							if (subType.endsWith(" and")) subType=subType.substring(0, subType.length()-4);

							int pos=subType.indexOf("(as ");
							if (pos>=0)
							{
								int closingBrace=StringUtils.findMatchingBrace(subType, pos);
								if (closingBrace>pos)
								{
									crewData.setListedAs(subType.substring(pos+4, closingBrace));
									subType=subType.substring(0, pos).trim();
								}
							}
							if (subType.startsWith("(") && subType.endsWith(")")) subType=subType.substring(1, subType.length()-1);
							subType=WordUtils.capitalize(subType);
							crewData.setSubType(subType);
						}
						movieData.addCrew(crewData);
					}
				}
			}
		}

		TableTag castTableTag=(TableTag) HtmlUtils.findFirst((CompositeTag) bodyNode, "table.cast");
		if (castTableTag!=null)
		{
			TableRow[] rows=castTableTag.getRows();
			for (int i=0; i<rows.length; i++)
			{
				TableRow row=rows[i];
				TableColumn[] columns=row.getColumns();
				String cell1=HtmlUtils.trimUnescape(columns[0].toPlainTextString());
				if ("rest of cast listed alphabetically:".equals(cell1)) continue;
				CastData castData=new CastData();
				castData.setCreditOrder(i+1);
				castData.setName(HtmlUtils.trimUnescape(columns[1].toPlainTextString()));
				LinkTag personLink=(LinkTag) HtmlUtils.findFirst(columns[1], "a");
				if (personLink!=null) castData.setKey(extractKeyFromLink(personLink));
				String role=HtmlUtils.trimUnescape(columns[3].toPlainTextString());
				int pos=role.indexOf("(as ");
				if (pos>=0)
				{
					int closingBrace=StringUtils.findMatchingBrace(role, pos);
					if (closingBrace>pos)
					{
						castData.setListedAs(role.substring(pos+4, closingBrace));
						role=role.substring(0, pos).trim();
					}
				}

				if (!"Extra".equals(role))
				{
					castData.setRole(role);
					movieData.addCast(castData);
				}
			}
		}
	}

	private String extractKeyFromLink(LinkTag personLink)
	{
		Matcher keyMatcher=nameLinkPattern.matcher(personLink.getAttribute("href"));
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
