package com.kiwisoft.media.tools;

import com.kiwisoft.html.HtmlUtils;
import com.kiwisoft.media.dataimport.ImportUtils;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.ParserException;

/**
 * @author Stefan Stiller
 * @since 24.07.2010
 */
public class FanFictionNetLoader
{
	private String url;

	public FanFictionNetLoader(String url)
	{
		this.url=url;
	}

	public static void main(String[] args) throws Exception
	{
		System.setProperty("kiwisoft.media.downloadCache", "true");
		new FanFictionNetLoader("http://www.fanfiction.net/s/4858657/1/Snow_Ball").load();
		new FanFictionNetLoader("http://www.fanfiction.net/s/5054111/1/I_Kissed_A_Girl").load();
	}

	public void load() throws IOException, ParserException
	{
		String url=ImportUtils.loadUrl(this.url);

		Parser parser=new Parser();
		parser.setInputHTML(url);

		Node formNode=HtmlUtils.findFirst(parser, new AndFilter(new TagNameFilter("FORM"), new HasAttributeFilter("name", "myselect")));
		Node scriptNode=HtmlUtils.findFirst((CompositeTag) formNode, "script");
		if (scriptNode!=null)
		{
			System.out.println("FanFictionNetLoader.load: formNode = "+scriptNode.toHtml());
			String script=scriptNode.toHtml();
			String title=find(script, "var\\s+title_t\\s+=\\s+'(.*)';", 1);
			String summary=find(script, "var\\s+summary\\s+=\\s+'(.*)';", 1);
			String categoryId=find(script, "var\\s+categoryid\\s+=\\s+'(.*)';", 1);
			String fandom=find(script, "var\\s+cat_title\\s+=\\s+'(.*)';", 1);
			String authorId=find(script, "var\\s+userid\\s+=\\s+'(.*)';", 1);
			String author=find(script, "var\\s+author\\s+=\\s+'(.*)';", 1);
			System.out.println("FanFictionNetLoader.load: title = "+title);
			System.out.println("FanFictionNetLoader.load: summary = "+summary);
			System.out.println("FanFictionNetLoader.load: author = "+author);
			System.out.println("FanFictionNetLoader.load: fandom = "+fandom);

		}
	}

	private String find(String text, String regEx, int group)
	{
		Pattern pattern=Pattern.compile(regEx);
		Matcher matcher=pattern.matcher(text);
		if (matcher.find()) return matcher.group(group);
		return null;
	}
}
