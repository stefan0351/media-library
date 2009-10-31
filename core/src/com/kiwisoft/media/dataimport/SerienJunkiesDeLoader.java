package com.kiwisoft.media.dataimport;

import static com.kiwisoft.media.dataimport.EpisodeData.DETAILS_LINK;
import com.kiwisoft.media.show.Show;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.html.HtmlUtils;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
public abstract class SerienJunkiesDeLoader extends EpisodeDataLoader
{
	public SimpleDateFormat airdateFormat;

	protected SerienJunkiesDeLoader(Show show, String baseUrl, int startSeason, int endSeason, boolean autoCreate)
	{
		super(show, baseUrl, startSeason, endSeason, autoCreate);
		Matcher matcher=Pattern.compile("(http://www.serienjunkies.de/[^/]+)/.*").matcher(baseUrl);
		if (matcher.matches()) setBaseUrl(matcher.group(1));
		airdateFormat=new SimpleDateFormat("dd.MM.yy");
		airdateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
	}

	@Override
	public String getName()
	{
		return "Load Episodes from SerienJunkies.de";
	}

	@Override
	protected List<EpisodeData> loadEpisodeList(int season) throws IOException, ParserException
	{
		String url=getBaseUrl()+"/season"+season+".html";
		log.debug("Loading season "+season+" from "+url);
		String page=ImportUtils.loadUrl(url);

		List<EpisodeData> episodes=new ArrayList<EpisodeData>();

		Parser parser=new Parser();
		parser.setInputHTML(page);
		CompositeTag tableNode=(CompositeTag) HtmlUtils.findFirst(parser, "table.eplist");
		NodeList rowNodes=HtmlUtils.findAll(tableNode, "tr");
		for (NodeIterator it=rowNodes.elements(); it.hasMoreNodes();)
		{
			if (getProgress().isStoppedByUser()) return null;
			CompositeTag rowNode=(CompositeTag) it.nextNode();
			try
			{
				NodeList cellNodes=HtmlUtils.findAll(rowNode, "td");
				CompositeTag numberNode=(CompositeTag) cellNodes.elementAt(0);
				CompositeTag titleNode=(CompositeTag) cellNodes.elementAt(2);
				CompositeTag germanTitleNode=(CompositeTag) cellNodes.elementAt(3);
				CompositeTag infoNode=(CompositeTag) cellNodes.elementAt(8);

				String episodeKey=season+"."+HtmlUtils.trimUnescape(numberNode.toPlainTextString());
				log.debug("Episode key: "+episodeKey);
				String title=HtmlUtils.trimUnescape(titleNode.toPlainTextString());
				log.debug("Title: "+title);
				EpisodeData episodeData=new EpisodeData(episodeKey, title);

				episodeData.setGermanTitle(HtmlUtils.trimUnescape(germanTitleNode.toPlainTextString()));
				log.debug("German title: "+episodeData.getGermanTitle());

				Tag linkNode=(Tag) HtmlUtils.findFirst(infoNode, "a");
				if (linkNode!=null)
				{
					episodeData.setLink(DETAILS_LINK, new URL(new URL(getBaseUrl()), linkNode.getAttribute("href")).toString());
					log.debug("Details link: "+episodeData.getLink(DETAILS_LINK));
				}

				episodes.add(episodeData);
			}
			catch (Exception e)
			{
				getProgress().error(e);
			}
		}
		return episodes;
	}

	@Override
	protected void loadDetails(EpisodeData episodeData) throws IOException, ParserException
	{
		log.debug("Loading details for "+episodeData.getTitle());
		String page=ImportUtils.loadUrl(episodeData.getLink(DETAILS_LINK));

		Parser parser=new Parser();
		parser.setInputHTML(page);

		CompositeTag tableNode=(CompositeTag) HtmlUtils.findFirst(parser, "table#epdetails");
		CompositeTag summaryCell=(CompositeTag) HtmlUtils.findFirst(tableNode, "td[colspan=\"2\"]");
		if (summaryCell!=null)
		{
			StringBuilder summary=new StringBuilder();
			NodeList paragraphs=HtmlUtils.findAll(summaryCell, "p");
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
