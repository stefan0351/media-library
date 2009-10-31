package com.kiwisoft.media.download;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.kiwisoft.utils.FileUtils;
import com.kiwisoft.utils.StringUtils;

public class CSSParser implements Parser
{
	private final static Pattern URL_PATTERN=Pattern.compile("url\\(");

	@Override
	public void parse(File file, URL url, List<URL> contained, List<URL> linked) throws IOException
	{
		String content=FileUtils.loadFile(file);
		Matcher matcher=URL_PATTERN.matcher(content);
		int index=0;
		while (matcher.find(index))
		{
			int start=matcher.start()+3;
			int end=StringUtils.findMatchingBrace(content, start);
			if (end>start)
			{
				String path=content.substring(start+1, end);
				URL newURL=GrabberUtils.getRelativeURL(url, path);
				if (newURL!=null && !ExcludeFilter.getInstance().match(newURL.toString()))
				{
					if (contained!=null) contained.add(newURL);
				}
			}
			index=matcher.end();
		}
	}
}
