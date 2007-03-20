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
public class BodyTag extends TagSupport
{
	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			JspWriter out=pageContext.getOut();
			out.println("<div class=\"main\">");
			out.println("<table cellspacing=\"0\" cellpadding=\"5\"><tr valign=\"top\">");
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
			out.write("</tr></table>");
			out.write("</div>");
			return EVAL_PAGE;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}
}
