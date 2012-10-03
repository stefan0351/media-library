package com.kiwisoft.media.dataimport;

import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.html.LinkTextFilter;
import com.kiwisoft.utils.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 * @since 07.12.2010
 */
public class FanFictionNetLoader
{
	public static final Pattern URL_PATTERN=Pattern.compile("(http://www.fanfiction.net/s/[0-9]+)\\b.*");

	private String firstPage;
	private String baseUrl;

	public FanFictionNetLoader(String url)
	{
		Matcher matcher=URL_PATTERN.matcher(url);
		if (matcher.matches()) baseUrl=matcher.group(1);
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public FanFicData getInfo() throws Exception
	{
		firstPage=ImportUtils.loadUrl(baseUrl, "UTF-8");
		return parseInfo(firstPage);
	}

	public String getChapter(int chapter) throws IOException, ParserException
	{
		String page=chapter==1 && firstPage!=null ? firstPage : ImportUtils.loadUrl(baseUrl+"/"+chapter, "UTF-8");

		Parser parser=new Parser();
		parser.setInputHTML(page);
		Node bodyNode=HtmlUtils.findFirst(parser, "body");
		Node storyNode=HtmlUtils.findFirst((CompositeTag) bodyNode, "div.storytext");
		String html=HtmlUtils.getInnerHtml((TagNode) storyNode);
		html=html.replace("<br>", "<br>\n");
		html=html.replace("</p>", "</p>\n");
		return html;
	}

	private FanFicData parseInfo(String firstPage) throws Exception
	{
		FanFicData fanFic=new FanFicData();

		Parser parser=new Parser();
		parser.setInputHTML(firstPage);
		Node bodyNode=HtmlUtils.findFirst(parser, "body");

		TableTag tableNode=(TableTag) HtmlUtils.findFirst((CompositeTag) bodyNode, "#gui_table1i");
		String infoHTML=tableNode.getChildrenHTML();
		System.out.println("infoHTML = "+infoHTML);
		Matcher matcher=Pattern.compile("<b>(.*)</b>").matcher(infoHTML);
		if (matcher.find()) fanFic.setTitle(HtmlUtils.trimUnescape(matcher.group(1)));
		matcher=Pattern.compile("<b>(.*)</b>").matcher(infoHTML);
		if (matcher.find()) fanFic.setTitle(HtmlUtils.trimUnescape(matcher.group(1)));
		matcher=Pattern.compile("Author: <a href='/u/(\\d+)/.*?'>(.*?)</a>").matcher(infoHTML);
		if (matcher.find()) fanFic.setAuthor(HtmlUtils.trimUnescape(matcher.group(2)));

		Node formNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("form"), new HasAttributeFilter("name", "myselect")));
		ScriptTag scriptNode=(ScriptTag) HtmlUtils.findFirst((CompositeTag) formNode, "script");
		String script=scriptNode.getScriptCode();

		fanFic.setComplete(infoHTML.contains("Status: Complete"));
		Node selectNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("select"), new HasAttributeFilter("id", "chap_select")));
		if (selectNode!=null)
		{
			NodeList optionNodes=HtmlUtils.findAll((CompositeTag) selectNode, "option");
			List<String> chapters=new ArrayList<String>(optionNodes.size());
			int i=1;
			for (NodeIterator it=optionNodes.elements(); it.hasMoreNodes();)
			{
				OptionTag optionNode=(OptionTag) it.nextNode();
				String chapterNumber=(i++)+". ";
				String chapterTitle=optionNode.toPlainTextString();
				if (chapterTitle.startsWith(chapterNumber)) chapterTitle=chapterTitle.substring(chapterNumber.length());
				chapters.add(chapterTitle);
			}
			fanFic.setChapterCount(chapters.size());
			fanFic.setChapters(chapters);
		}
		else fanFic.setChapterCount(1);

/*		if (domain!=null)
		{
			LinkTag linkNode=(LinkTag) HtmlUtils.findFirst((CompositeTag) bodyNode, new LinkTextFilter(domain));
			if (linkNode!=null)
			{
				String url=new URL(new URL(baseUrl), linkNode.getAttribute("href")).toString();
				fanFic.setDomainUrl(url);
			}
			if (domain.endsWith(" Crossover") && fanFic.getDomainUrl()!=null)
			{
				String crossoverPage=ImportUtils.loadUrl(fanFic.getDomainUrl());
				parseCrossoverDomain(crossoverPage, fanFic);
			}
		}

  */
		Node ratingNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href", "http://www.fictionratings.com/")));
		if (ratingNode!=null)
		{
			fanFic.setRating(HtmlUtils.trimUnescape(ratingNode.toPlainTextString()));
		}

		matcher=Pattern.compile("Rated: <a[^>]+>[^<]+</a> - (.*) - (.*) - (.*) - Reviews:").matcher(infoHTML);
		if (matcher.find())
		{
			fanFic.setLanguage(matcher.group(1));
			fanFic.setGenres(Arrays.asList(StringUtils.splitAndTrim(HtmlUtils.trimUnescape(matcher.group(2)), "/")));
			fanFic.setCharacters(Arrays.asList(StringUtils.splitAndTrim(HtmlUtils.trimUnescape(matcher.group(3)), " & ")));
		}

		matcher=Pattern.compile("Published: (\\d\\d-\\d\\d-\\d\\d)").matcher(infoHTML);
		if (matcher.find()) fanFic.setPublishedDate(new SimpleDateFormat("MM-dd-yy").parse(matcher.group(1)));
		return fanFic;
	}

	private void parseCrossoverDomain(String page, FanFicData fanFic) throws ParserException
	{
		Parser parser=new Parser();
		parser.setInputHTML(page);
		Node bodyNode=HtmlUtils.findFirst(parser, "body");

		Set<String> domains=new HashSet<String>();
		NodeList list=HtmlUtils.findAll((CompositeTag) bodyNode, new LinkRegexFilter("/crossovers/.*/[0-9]+/", false));
		for (NodeIterator it=list.elements(); it.hasMoreNodes();)
		{
			LinkTag link=(LinkTag) it.nextNode();
			domains.add(HtmlUtils.trimUnescape(link.getLinkText()));
		}
		fanFic.setDomains(domains);

	}
}
