package com.kiwisoft.media.tags;

import java.io.IOException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * @author Stefan Stiller
 */
public class SideBarTag extends TagSupport
{
	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			JspWriter out=pageContext.getOut();
			out.println("<td width=\"200\">");
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
			out.write("</td>");
			return EVAL_PAGE;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}
}
