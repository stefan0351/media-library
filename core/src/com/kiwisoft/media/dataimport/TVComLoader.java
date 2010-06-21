package com.kiwisoft.media.dataimport;

import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.utils.StringNumberComparator;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.progress.ProgressSupport;
import com.kiwisoft.media.person.CreditType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 */
public class TVComLoader implements EpisodeLoader
{
	private final static Log log=LogFactory.getLog(TVComLoader.class);

	private SimpleDateFormat airdateFormat;
	private Pattern personLinkPattern;
	private String baseUrl;

	public TVComLoader(String baseUrl)
	{
		Matcher matcher=Pattern.compile(("(http://www.tv.com/.*/show/[0-9]+/).*")).matcher(baseUrl);
		if (matcher.matches()) this.baseUrl=matcher.group(1);
		else this.baseUrl=baseUrl;
		airdateFormat=new SimpleDateFormat("M/d/yyyy");
		personLinkPattern=Pattern.compile("http://www.tv.com/[^/]+/person/(\\d+)/.*");
	}

	@Override
	public String getName()
	{
		return "TV.com";
	}

	@Override
	public boolean hasGermanData()
	{
		return false;
	}

	@Override
	public List<EpisodeData> loadList(ProgressSupport progressSupport) throws Exception
	{
		String url=baseUrl+"episode.html?shv=list&season=All";
		log.debug("Loading episodes from "+url);
		String page=ImportUtils.loadUrl(url);

		List<EpisodeData> episodes=new ArrayList<EpisodeData>();

		Parser parser=new Parser();
		parser.setInputHTML(page);
		Div mainDiv=(Div) HtmlUtils.findFirst(parser, "div#episode_listing");
		if (mainDiv!=null)
		{
			log.debug("div#episode_listing found");
			NodeList episodeRows=HtmlUtils.findAll(mainDiv, "tr.episode");
			for (NodeIterator it=episodeRows.elements(); it.hasMoreNodes();)
			{
				if (progressSupport.isStoppedByUser()) return null;
				try
				{
					TableRow row=(TableRow) it.nextNode();
					Node numberCell=HtmlUtils.findFirst(row, "td.number");
					String episodeKey=HtmlUtils.trimUnescape(numberCell.toPlainTextString());
					log.debug("Episode key: "+episodeKey);

					TableColumn titleCell=(TableColumn) HtmlUtils.findFirst(row, "td.title");
					String episodeTitle=HtmlUtils.trimUnescape(titleCell.toPlainTextString());
					log.debug("Episode title: "+episodeTitle);

					TagNode linkNode=(TagNode) HtmlUtils.findFirst(titleCell, "a");
					String episodeLink=null;
					if (linkNode!=null) episodeLink=linkNode.getAttribute("href");
					log.debug("Details link: "+episodeLink);

					Node airdateCell=HtmlUtils.findFirst(row, "td.air_date");
					Date airdate=null;
					try
					{
						airdate=airdateFormat.parse(HtmlUtils.trimUnescape(airdateCell.toPlainTextString()));
						if (airdate.getTime()<0L) airdate=null;
					}
					catch (ParseException e)
					{
						progressSupport.error(e);
					}
					log.debug("Airdate: "+airdate);

					Node prodNoCell=HtmlUtils.findFirst(row, "td.prod_no");
					String prodNo=HtmlUtils.trimUnescape(prodNoCell.toPlainTextString());
					log.debug("Production No.: "+prodNo);

					EpisodeData episodeData=new EpisodeData(episodeKey, episodeTitle, airdate, prodNo);
					episodeData.setLink(EpisodeData.DETAILS_LINK, episodeLink);
					episodes.add(episodeData);
				}
				catch (ParserException e)
				{
					progressSupport.error(e);
				}
				Collections.sort(episodes, new Comparator<EpisodeData>()
				{
					private Comparator<String> keyComparator=new StringNumberComparator();

					@Override
					public int compare(EpisodeData o1, EpisodeData o2)
					{
						return keyComparator.compare(o1.getKey(), o2.getKey());
					}
				});
			}
		}
		else
		{
			progressSupport.warning("Invalid page content (episode_listing).");
		}
		return episodes;
	}

	@Override
	public void loadDetails(ProgressSupport progressSupport, EpisodeData episodeData) throws Exception
	{
		try
		{
			loadSummary(episodeData);

			String baseUrl=episodeData.getLink(EpisodeData.DETAILS_LINK);
			baseUrl=baseUrl.substring(0, baseUrl.lastIndexOf("/")+1);
			episodeData.setLink("cast", baseUrl+"cast.html");
			episodeData.setLink("crew", baseUrl+"cast.html?flag=6");

			loadCast(progressSupport, episodeData);
			loadCrew(progressSupport, episodeData);
		}
		finally
		{
			episodeData.setDetailsLoaded(true);
		}
	}

	private void loadSummary(EpisodeData episodeData) throws ParserException, IOException
	{
		log.debug("Loading summary for "+episodeData.getTitle());
		String page=ImportUtils.loadUrl(episodeData.getLink(EpisodeData.DETAILS_LINK));
		Parser parser=new Parser();
		parser.setInputHTML(page);
		Div crumbsNode=(Div) HtmlUtils.findFirst(parser, "div.crumbs");
		if (crumbsNode!=null)
		{
			Node episodeKeyNode=HtmlUtils.findFirst(crumbsNode, "span");
			if (episodeKeyNode!=null)
			{
				Matcher matcher=Pattern.compile("Season (\\d+), Episode (\\d+)").matcher(HtmlUtils.trimUnescape(episodeKeyNode.toPlainTextString()));
				if (matcher.matches())
				{
					String oldKey=episodeData.getKey();
					episodeData.setKey(matcher.group(1)+"."+matcher.group(2));
					log.debug("Episode key: changed from "+oldKey+" to "+episodeData.getKey());
				}
			}
		}
		parser.reset();
		Div recapNode=(Div) HtmlUtils.findFirst(parser, "div#episode_recap");
		if (recapNode!=null)
		{
			StringBuilder summary=new StringBuilder();
			NodeList paragraphs=HtmlUtils.findAll(recapNode, "p");
			for (NodeIterator it=paragraphs.elements(); it.hasMoreNodes();)
			{
				TagNode paragraph=(TagNode) it.nextNode();
				String text=ImportUtils.toPreformattedText(paragraph.toHtml(false), true);
				if (!StringUtils.isEmpty(text))
				{
					if (summary.length()>0) summary.append("[br/][br/]\n");
					summary.append(text);
				}
			}
			log.debug("Summary: "+summary);
			episodeData.setEnglishSummary(summary.toString());
		}
	}

	private Pattern mainCastPattern=Pattern.compile("Stars?");
	private Pattern recurringCastPattern=Pattern.compile("Recurring Roles?");
	private Pattern guestCastPattern=Pattern.compile("((Special )?Guest Stars?)|(Cameos?)");

	private void loadCast(ProgressSupport progressSupport, EpisodeData episodeData) throws IOException, ParserException
	{
		log.debug("Loading cast for "+episodeData.getTitle());
		String page=ImportUtils.loadUrl(episodeData.getLink("cast"));
		Parser parser=new Parser();
		parser.setInputHTML(page);

		CompositeTag mainNode=(CompositeTag) HtmlUtils.findFirst(parser, "div#cast_crew_list");
		NodeList listNodes=HtmlUtils.findAll(mainNode, "div.list");
		for (NodeIterator it=listNodes.elements(); it.hasMoreNodes();)
		{
			CompositeTag listNode=(CompositeTag) it.nextNode();
			CompositeTag typeNode=(CompositeTag) HtmlUtils.findFirst(listNode, "h3");
			String type=HtmlUtils.trimUnescape(typeNode.toPlainTextString());
			log.debug("Cast type: "+type);
			NodeList itemNodes=HtmlUtils.findAll(listNode, "li.person");
			for (NodeIterator it2=itemNodes.elements(); it2.hasMoreNodes();)
			{
				CompositeTag itemNode=(CompositeTag) it2.nextNode();
				try
				{
					CompositeTag nameNode=(CompositeTag) HtmlUtils.findFirst(itemNode, ".full_name");
					if (nameNode!=null)
					{
						String actor=HtmlUtils.trimUnescape(nameNode.toPlainTextString());
						String key=null;
						CompositeTag linkNode=(CompositeTag) HtmlUtils.findFirst(nameNode, "a");
						if (linkNode!=null)
						{
							Matcher matcher=personLinkPattern.matcher(linkNode.getAttribute("href"));
							if (matcher.matches())
							{
								key=matcher.group(1);
							}
						}
						log.debug("Actor: "+actor);
						log.debug("Key: "+key);
						if (!StringUtils.isEmpty(actor))
						{
							CompositeTag roleNode=(CompositeTag) HtmlUtils.findFirst(itemNode, ".role");
							String role=null;
							if (roleNode!=null) role=HtmlUtils.trimUnescape(roleNode.toPlainTextString());
							log.debug("Role: "+role);
							CastData castData=new CastData(actor, role, null, key);
							if (mainCastPattern.matcher(type).matches()) episodeData.addMainCast(castData);
							else if (recurringCastPattern.matcher(type).matches()) episodeData.addRecurringCast(castData);
							else if (guestCastPattern.matcher(type).matches()) episodeData.addGuestCast(castData);
							else
							{
								progressSupport.warning("Unknown cast type: "+type);
								log.warn("Unknown cast type: "+type);
							}
						}
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					progressSupport.error(e);
				}
			}
		}
	}

	private void loadCrew(ProgressSupport progressSupport, EpisodeData episodeData) throws IOException, ParserException
	{
		log.debug("Loading crew for "+episodeData.getTitle());
		String page=ImportUtils.loadUrl(episodeData.getLink("crew"));
		Parser parser=new Parser(page);
		parser.setInputHTML(page);

		CompositeTag mainNode=(CompositeTag) HtmlUtils.findFirst(parser, "div#cast_crew_list");
		NodeList listNodes=HtmlUtils.findAll(mainNode, "div.list");
		for (NodeIterator it=listNodes.elements(); it.hasMoreNodes();)
		{
			CompositeTag listNode=(CompositeTag) it.nextNode();
			CompositeTag typeNode=(CompositeTag) HtmlUtils.findFirst(listNode, "h3");
			String type=HtmlUtils.trimUnescape(typeNode.toPlainTextString());
			log.debug("Crew type: "+type);
			NodeList itemNodes=HtmlUtils.findAll(listNode, "li.person");
			for (NodeIterator it2=itemNodes.elements(); it2.hasMoreNodes();)
			{
				CompositeTag itemNode=(CompositeTag) it2.nextNode();
				try
				{
					CompositeTag nameNode=(CompositeTag) HtmlUtils.findFirst(itemNode, ".full_name");
					if (nameNode!=null)
					{
						String person=HtmlUtils.trimUnescape(nameNode.toPlainTextString());
						String key=null;
						CompositeTag linkNode=(CompositeTag) HtmlUtils.findFirst(nameNode, "a");
						if (linkNode!=null)
						{
							Matcher matcher=personLinkPattern.matcher(linkNode.getAttribute("href"));
							if (matcher.matches())
							{
								key=matcher.group(1);
							}
						}
						log.debug("Person: "+person);
						log.debug("Key: "+key);
						if (!StringUtils.isEmpty(person))
						{
							CompositeTag roleNode=(CompositeTag) HtmlUtils.findFirst(itemNode, ".role");
							String role=null;
							if (roleNode!=null) role=HtmlUtils.trimUnescape(roleNode.toPlainTextString());
							if (role!=null && role.endsWith("(n/a)")) role=role.substring(0, role.length()-"(n/a)".length()).trim();
							log.debug("Role: "+role);
							if ("Writer".equalsIgnoreCase(type) || "Writers".equalsIgnoreCase(type))
							{
								if ("Writer".equalsIgnoreCase(role))
									episodeData.addWrittenBy(new CrewData(person, CreditType.WRITER, null, key));
								else
									episodeData.addWrittenBy(new CrewData(person, CreditType.WRITER, role, key));
							}
							else if ("Director".equalsIgnoreCase(type))
							{
								if ("Director".equalsIgnoreCase(role))
									episodeData.addDirectedBy(new CrewData(person, CreditType.DIRECTOR, null, key));
								else
									episodeData.addDirectedBy(new CrewData(person, CreditType.DIRECTOR, role, key));
							}
							else if (!"Crew".equalsIgnoreCase(type))
							{
								progressSupport.warning("Unknown cast type: "+type);
								log.warn("Unknown cast type: "+type);
							}
						}
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
					progressSupport.error(e);
				}
			}
		}
	}
}