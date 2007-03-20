package com.kiwisoft.media.tags;

import java.io.IOException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 28.02.2007
 * Time: 18:15:07
 * To change this template use File | Settings | File Templates.
 */
public class PanelTag extends TagSupport
{
	private String title;

	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			JspWriter out=pageContext.getOut();
			out.println("<table class=\"contenttable\" width=\"790\">");
			out.print("<tr><td class=\"header1\">");
			out.print(title);
			out.println("</td></tr>");
			out.println("<tr><td class=\"content\">");
			return EVAL_BODY_INCLUDE;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}

	@Override
	public int doEndTag() throws JspException
	{
		try
		{
			JspWriter out=pageContext.getOut();
			out.println("<p align=\"right\"><a class=\"link\" href=\"#top\">Top</a></p>");
			out.println("</td></tr>");
			out.println("</table>");
			return EVAL_PAGE;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}

	public void setTitle(String title)
	{
		this.title=title;
	}

	@Override
	public void release()
	{
		title=null;
		super.release();
	}
}
