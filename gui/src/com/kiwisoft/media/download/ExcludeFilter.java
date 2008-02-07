/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Jan 2, 2003
 * Time: 1:30:31 PM
 */
package com.kiwisoft.media.download;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class ExcludeFilter
{
	private static ExcludeFilter instance;

	private Map<String, Pattern> expressions;

	private ExcludeFilter()
	{
		expressions=new HashMap<String, Pattern>();
		addExpression(".*\\.rb$");
	}

	public static ExcludeFilter getInstance()
	{
		if (instance==null) instance=new ExcludeFilter();
		return instance;
	}

	public void addExpression(String expression)
	{
		if (!expressions.containsKey(expression))
		{
			Pattern pattern=Pattern.compile(expression);
			expressions.put(expression, pattern);
		}
	}

	public void removeExpression(String expression)
	{
		expressions.remove(expression);
	}

	public boolean match(String text)
	{
		Iterator<Pattern> it=expressions.values().iterator();
		while (it.hasNext())
		{
			Pattern pattern=it.next();
			if (!pattern.matcher(text).matches()) return false;
		}
		return true;
	}

}
