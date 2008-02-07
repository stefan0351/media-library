package com.kiwisoft.media.download;

public class ParserFactory
{
	public static Parser getParser(String type)
	{
		if (type.startsWith("text/html")) return new HTMLParser();
		if (type.startsWith("text")) return new TextParser();
		return null;
	}

	public static boolean isParsable(String type)
	{
		if (type.startsWith("text/html")) return true;
		if (type.startsWith("text")) return true;
		return false;
	}

	private ParserFactory()
	{
	}
}