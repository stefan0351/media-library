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

import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.SimpleNodeIterator;

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

	public static void main(String[] args) throws IOException, ParserException
	{
		Locale.setDefault(Locale.UK);
		SimpleConfiguration configuration=new SimpleConfiguration();
		File configFile=new File("conf", "config.xml");
		configuration.loadDefaultsFromFile(configFile);
		configuration.loadUserValues("media"+File.separator+"dev-profile.xml");

		FanFictionNetLoader loader=new FanFictionNetLoader("http://www.fanfiction.net/s/5729523/1/Uninvited");
		FanFicData data=loader.getInfo();
		System.out.println(data.getTitle());
		System.out.println(data.getChapterCount());
		System.out.println(data.getChapters());
		System.out.println(data.getSummary());
		System.out.println(data.isComplete());
		for (int i=1;i<=data.getChapterCount();i++)
		{
			System.out.println(i+". "+(data.getChapterCount()>1 ? data.getChapters().get(i-1) : data.getTitle()));
			String chapter=loader.getChapter(i);
			System.out.println(chapter);
		}
	}

	public FanFicData getInfo() throws IOException, ParserException
	{
		firstPage=ImportUtils.loadUrl(baseUrl, "UTF-8");
		return parseInfo(firstPage);
	}

	public String getChapter(int chapter) throws IOException, ParserException
	{
		String page=chapter==1 && firstPage!=null ? firstPage : ImportUtils.loadUrl(baseUrl+"/"+chapter);

		Parser parser=new Parser();
		parser.setInputHTML(page);
		Node bodyNode=HtmlUtils.findFirst(parser, "body");
		Node storyNode=HtmlUtils.findFirst((CompositeTag) bodyNode, "div.storytext");
		String html=getInnerHtml((TagNode) storyNode);
		html=html.replace("<br>", "<br>\n");
		html=html.replace("</p>", "</p>\n");
		return html;
	}

	private FanFicData parseInfo(String firstPage) throws ParserException
	{
		Parser parser=new Parser();
		parser.setInputHTML(firstPage);
		Node bodyNode=HtmlUtils.findFirst(parser, "body");
		Node formNode=HtmlUtils.findFirst((CompositeTag) bodyNode, new AndFilter(new TagNameFilter("form"), new HasAttributeFilter("name", "myselect")));
		ScriptTag scriptNode=(ScriptTag) HtmlUtils.findFirst((CompositeTag) formNode, "script");
		String script=scriptNode.getScriptCode();

		FanFicData fanFic=new FanFicData();
		fanFic.setChapterCount(Integer.parseInt(RegExUtils.find(script, "var chapters\\s*=\\s*([0-9]+);", 1)));
		fanFic.setSummary(RegExUtils.find(script, "var summary\\s*=\\s*'(.*)';", 1));
		fanFic.setTitle(RegExUtils.find(script, "var title_t\\s*=\\s*'(.*)';", 1));
		fanFic.setComplete(RegExUtils.find(firstPage, " - (Complete) - id:[0-9]+ ", 1)!=null);

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
		return fanFic;
	}

	/**
	 * Add the textual contents of the children of this node to the buffer.
	 */
	private String getInnerHtml(TagNode tagNode)
	{
		StringBuilder buffer=new StringBuilder();
		Node node;
		for (SimpleNodeIterator e = tagNode.getChildren().elements(); e.hasMoreNodes ();)
		{
			node = e.nextNode ();
			buffer.append(node.toHtml(true));
		}
		return buffer.toString();
	}

}
