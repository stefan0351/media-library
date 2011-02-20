package com.kiwisoft.utils.websearch;

import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.media.dataimport.ImportUtils;
import com.kiwisoft.utils.HttpConstants;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Stefan Stiller
 * @since 19.02.11
 */
public class GoogleSearch
{
	private HashMap<String, String> connectionProperties;
	private String site;
	private Integer resultsPerPage;

	public GoogleSearch()
	{
		connectionProperties=new HashMap<String, String>();
		connectionProperties.put(HttpConstants.USER_AGENT, HttpConstants.AGENT_FIREFOX_3);
	}

	public List<WebSearchResult> search(String expression) throws IOException, ParserException
	{
		List<WebSearchResult> results=new ArrayList<WebSearchResult>();

		if (site!=null) expression=expression+" site:"+site;
		String url="http://www.google.de/search?q="+URLEncoder.encode(expression, "UTF-8");
		if (resultsPerPage!=null) url=url+"&num="+resultsPerPage;
		String page=ImportUtils.loadUrl(url, connectionProperties, "UTF-8");

		Parser parser=new Parser();
		parser.setInputHTML(page);

		Node resultListNode=HtmlUtils.findFirst(parser, "#ires");
		if (resultListNode==null) return results;

		NodeList resultNodes=HtmlUtils.findAll((CompositeTag) resultListNode, "div.vsc");
		for (NodeIterator it=resultNodes.elements(); it.hasMoreNodes();)
		{
			CompositeTag resultNode=(CompositeTag) it.nextNode();
			try
			{
				CompositeTag headingNode=(CompositeTag) HtmlUtils.findFirst(resultNode, "h3.r");
				if (headingNode!=null)
				{
					CompositeTag linkNode=(CompositeTag) HtmlUtils.findFirst(headingNode, "a");
					CompositeTag descriptionNode=(CompositeTag) HtmlUtils.findFirst(resultNode, "div.s");
					String descriptionHtml=getInnerHtml(descriptionNode);
					int index=descriptionHtml.indexOf("<br>");
					if (index>=0) descriptionHtml=descriptionHtml.substring(0, index);

					WebSearchResult result=new WebSearchResult(HtmlUtils.trimUnescape(headingNode.toPlainTextString()), linkNode.getAttribute("href"));
					result.setDescription(descriptionHtml);
					results.add(result);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return results;
	}

	public void setSite(String site)
	{
		this.site=site;
	}

	private static String getInnerHtml(CompositeTag tag)
	{
		StringBuilder html=new StringBuilder();
		for (SimpleNodeIterator e=tag.children(); e.hasMoreNodes();)
		{
			html.append(e.nextNode().toHtml(true));
		}
		return html.toString();

	}

	public void setResultsPerPage(int resultsPerPage)
	{
		this.resultsPerPage=resultsPerPage;
	}
}
