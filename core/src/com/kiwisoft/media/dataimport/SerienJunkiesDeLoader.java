package com.kiwisoft.media.dataimport;

import com.kiwisoft.html.HtmlUtils;
import static com.kiwisoft.media.dataimport.EpisodeData.DETAILS_LINK;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
public class SerienJunkiesDeLoader implements EpisodeLoader
{
	private final static Log log=LogFactory.getLog(SerienJunkiesDeLoader.class);

	private String baseUrl;
	private Pattern keyPattern=Pattern.compile("(\\d+)x0*(\\d+)");

	public SerienJunkiesDeLoader(String baseUrl)
	{
		this.baseUrl=baseUrl;
		Matcher matcher=Pattern.compile("(http://www.serienjunkies.de/[^/]+)/.*").matcher(baseUrl);
		if (matcher.matches()) this.baseUrl=matcher.group(1);
	}

	@Override
	public String getName()
	{
		return "SerienJunkies.de";
	}

	@Override
	public boolean hasGermanData()
	{
		return true;
	}

	@Override
	public List<EpisodeData> loadList(ProgressSupport progressSupport) throws Exception
	{
		String url=baseUrl+"/alle-serien-staffeln.html";
		log.debug("Loading episodes from "+url);
		String page=ImportUtils.loadUrl(url);

		List<EpisodeData> episodes=new ArrayList<EpisodeData>();

		Parser parser=new Parser();
		parser.setInputHTML(page);
		CompositeTag tableNode=(CompositeTag) HtmlUtils.findFirst(parser, "table.eplist");
		NodeList rowNodes=HtmlUtils.findAll(tableNode, "tr");
		for (NodeIterator it=rowNodes.elements(); it.hasMoreNodes();)
		{
			if (progressSupport.isStoppedByUser()) return null;
			CompositeTag rowNode=(CompositeTag) it.nextNode();
			try
			{
				NodeList cellNodes=HtmlUtils.findAll(rowNode, "td");
				if (cellNodes!=null && cellNodes.size()>0)
				{
					CompositeTag numberNode=(CompositeTag) cellNodes.elementAt(0);
					String cssClass=numberNode.getAttribute("class");
					if (cssClass!=null && cssClass.endsWith("folge"))
					{
						String episodeKey=HtmlUtils.trimUnescape(numberNode.toPlainTextString());
						Matcher matcher=keyPattern.matcher(episodeKey);
						if (matcher.matches()) episodeKey=matcher.group(1)+"."+matcher.group(2);
						log.debug("Episode key: "+episodeKey);

						CompositeTag titleNode=(CompositeTag) cellNodes.elementAt(2);
						CompositeTag germanTitleNode=(CompositeTag) cellNodes.elementAt(4);
						CompositeTag infoNode=(CompositeTag) cellNodes.elementAt(7);


						String title=HtmlUtils.trimUnescape(titleNode.toPlainTextString());
						log.debug("Title: "+title);
						EpisodeData episodeData=new EpisodeData(episodeKey, title);

						episodeData.setGermanTitle(HtmlUtils.trimUnescape(germanTitleNode.toPlainTextString()));
						log.debug("German title: "+episodeData.getGermanTitle());

						Tag linkNode=(Tag) HtmlUtils.findFirst(infoNode, "a");
						if (linkNode!=null)
						{
							episodeData.setLink(DETAILS_LINK, new URL(new URL(baseUrl), linkNode.getAttribute("href")).toString());
							log.debug("Details link: "+episodeData.getLink(DETAILS_LINK));
						}

						episodes.add(episodeData);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				progressSupport.error(e);
			}
		}
		return episodes;
	}

	@Override
	public void loadDetails(ProgressSupport progressSupport, EpisodeData episodeData) throws Exception
	{
		log.debug("Loading details for "+episodeData.getTitle());
		String page=ImportUtils.loadUrl(episodeData.getLink(DETAILS_LINK));

		Parser parser=new Parser();
		parser.setInputHTML(page);

		CompositeTag summaryNode=(CompositeTag) HtmlUtils.findFirst(parser, "span.summary");
		if (summaryNode!=null)
		{
			StringBuilder summary=new StringBuilder();
			NodeList paragraphs=HtmlUtils.findAll(summaryNode, "p");
			for (NodeIterator it=paragraphs.elements(); it.hasMoreNodes();)
			{
				TagNode paragraph=(TagNode) it.nextNode();
				String text=ImportUtils.toPreformattedText(paragraph.toHtml(false), true);
				if (text.startsWith("Wertung des Autors")) continue;
				if (!StringUtils.isEmpty(text))
				{
					if (summary.length()>0) summary.append("[br/][br/]\n");
					summary.append(text);
				}
			}
			log.debug("German: "+summary);
			episodeData.setGermanSummary(summary.toString());
		}
	}
}