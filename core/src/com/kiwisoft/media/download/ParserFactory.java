package com.kiwisoft.media.download;

public class ParserFactory
{
	public static Parser getParser(String type)
	{
		if (type==null) return null;
		if (type.startsWith("text/html")) return new HTMLParser();
		if (type.startsWith("text/css")) return new CSSParser();
		if (type.startsWith("text")) return new TextParser();
		return null;
	}

	public static boolean isParsable(String type)
	{
		if (type==null) return false;
		if (type.startsWith("text/html")) return true;
		if (type.startsWith("text/css")) return true;
		if (type.startsWith("text")) return true;
		return false;
	}

	private ParserFactory()
	{
	}
}