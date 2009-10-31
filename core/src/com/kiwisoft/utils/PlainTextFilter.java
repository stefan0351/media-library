package com.kiwisoft.utils;

import org.htmlparser.NodeFilter;
import org.htmlparser.Node;

/**
 * @author Stefan Stiller
 * @since 25.10.2009
 * @todo move to utils
 */
public class PlainTextFilter implements NodeFilter
{
	private String text;

	public PlainTextFilter(String text)
	{
		this.text=text;
	}

	@Override
	public boolean accept(Node node)
	{
		if (node!=null)
			return this.text.equals(node.toPlainTextString());
		return false;
	}
}
