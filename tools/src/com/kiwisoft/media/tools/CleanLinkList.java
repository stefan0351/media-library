package com.kiwisoft.media.tools;

import com.kiwisoft.utils.RegExUtils;
import com.kiwisoft.utils.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stiller
 * @since 21.01.11
 */
public class CleanLinkList
{
	private final static String[] FORBIDDEN_WORDS={"euroshipping", "viagra", "teen-porn", "cheapmedshipping", "tdlmathscience.org"};

	private CleanLinkList()
	{
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		List<Integer> ids=new ArrayList<Integer>();
		String content=WebUtils.loadURL("http://www.tv-scripte.de/cgi/links/control.pl?page=links");
		Matcher matcher=Pattern.compile("<tr valign=top><td>([0-9]+)</td>").matcher(content);
		int minId=572;
		int index=0;
		while (matcher.find(index))
		{
			int id=Integer.parseInt(matcher.group(1));
			if (id>=minId) ids.add(id);
			index=matcher.end();
		}
		System.out.println("Ids: "+ids);

		for (Integer id : ids)
		{
			System.out.println("Check link: #"+id);
			content=WebUtils.loadURL("http://www.tv-scripte.de/cgi/links/control.pl?page=link&link="+id);
			String title=RegExUtils.find(content, "<input type=text name=\"title\" value=\"([^\"]*)\"", 1);
			String url=RegExUtils.find(content, "<input type=text name=\"url\" value=\"([^\"]*)\"", 1);
			String description=RegExUtils.find(content, "<textarea name=\"description\" cols=49 rows=4>(.*)</textarea>", 1);
			boolean spam=checkSpam(id, title) || checkSpam(id, url) || checkSpam(id, description);
			Thread.sleep(2000);
		}
	}

	private static boolean checkSpam(Integer id, String text) throws IOException
	{
		if (isSpam(text))
		{
			System.out.println("Delete Spam: "+text);
			WebUtils.loadURL("http://www.tv-scripte.de/cgi/links/control.pl?action=delete_link&link="+id+"&page=main");
			return true;
		}
		return false;
	}

	private static boolean isSpam(String text)
	{
		for (String forbiddenWord : FORBIDDEN_WORDS)
		{
			if (text.toLowerCase().contains(forbiddenWord)) return true;
		}
		return false;
	}
}
