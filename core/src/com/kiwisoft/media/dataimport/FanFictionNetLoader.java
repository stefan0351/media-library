package com.kiwisoft.media.dataimport;

import com.kiwisoft.utils.RegExUtils;
import com.kiwisoft.cfg.SimpleConfiguration;
import com.kiwisoft.html.HtmlUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.net.URL;

import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.NodeIterator;
import org.apache.commons.lang.StringEscapeUtils;

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
		Parser parser=new Parser();
		parser.setInputHTML(firstPage);
		Node bodyNode=HtmlUtils.findFirst(parser, "body");

		Node formNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("form"), new HasAttributeFilter("name", "myselect")));
		ScriptTag scriptNode=(ScriptTag) HtmlUtils.findFirst((CompositeTag) formNode, "script");
		String script=scriptNode.getScriptCode();

		FanFicData fanFic=new FanFicData();
		fanFic.setChapterCount(Integer.parseInt(RegExUtils.find(script, "var chapters\\s*=\\s*([0-9]+);", 1)));
		String summary=RegExUtils.find(script, "var\\s+summary\\s*=\\s*'(.*)'\\s*;", 1);
		if (summary!=null) fanFic.setSummary(StringEscapeUtils.unescapeJavaScript(summary));
		String title=RegExUtils.find(script, "var\\s+title_t\\s*=\\s*'(.*)'\\s*;", 1);
		if (title!=null) fanFic.setTitle(StringEscapeUtils.unescapeJavaScript(title));
		fanFic.setComplete(RegExUtils.find(firstPage, " - (Complete) - id:[0-9]+ ", 1)!=null);
		String authorId=RegExUtils.find(script, "var\\s+userid\\s*=\\s*([0-9]+)\\s*;", 1);
		if (authorId!=null) fanFic.setAuthorUrl("http://www.fanfiction.net/u/"+authorId);
		String author=RegExUtils.find(script, "var\\s+author\\s*=\\s*'(.*)'\\s*;", 1);
		if (author!=null) fanFic.setAuthor(StringEscapeUtils.unescapeJavaScript(author));


		if (!"1".equals(RegExUtils.find(script, "var chapters\\s*=\\s*([0-9]+);", 1)))
		{
			Node selectNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("select"), new HasAttributeFilter("title", "chapter navigation")));
			NodeList optionNodes=HtmlUtils.findAll((CompositeTag) selectNode, "option");
			List<String> chapters=new ArrayList<String>(optionNodes.size());
			int i=1;
			for (NodeIterator it=optionNodes.elements();it.hasMoreNodes();)
			{
				OptionTag optionNode=(OptionTag) it.nextNode();
				String chapterNumber=(i++)+". ";
				String chapterTitle=optionNode.toPlainTextString();
				if (chapterTitle.startsWith(chapterNumber)) chapterTitle=chapterTitle.substring(chapterNumber.length());
				chapters.add(chapterTitle);
			}
			fanFic.setChapters(chapters);
		}

		Node imageNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("img"), new HasAttributeFilter("src", "http://b.fanfiction.net/static/ficons/script.png")));
		if (imageNode!=null)
		{
			LinkTag linkTag=null;
			Node nextNode=imageNode;
			do
			{
				nextNode=nextNode.getNextSibling();
				if (nextNode instanceof LinkTag) linkTag=(LinkTag) nextNode;
			}
			while (nextNode!=null);
			if (linkTag!=null)
			{
				String url=new URL(new URL(baseUrl), linkTag.getAttribute("href")).toString();
				while (url.endsWith("/")) url=url.substring(0, url.length()-1);
				fanFic.setDomainUrl(url);
				fanFic.setDomain(linkTag.getLinkText());
			}

		}

		Node ratingNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("href", "http://www.fictionratings.com/")));
		if (ratingNode!=null)
		{
			fanFic.setRating(HtmlUtils.trimUnescape(ratingNode.toPlainTextString()));
		}
		return fanFic;
	}
}
