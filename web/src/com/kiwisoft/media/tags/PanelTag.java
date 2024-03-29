package com.kiwisoft.media.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

/**
 * @author Stefan Stiller
 */
public class PanelTag extends StrutsBodyTagSupport
{
	private String title;
	private String id;

	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			JspWriter out=pageContext.getOut();
			String title=findString(this.title);
			if (this.id!=null)
			{
				String id=findString(this.id);
				if (id!=null) out.println("<a name=\""+id+"\"/>");
			}
			out.println("<table class=\"contenttable\" width=\"790\">");
			out.print("<tr><td class=\"header1\">");
			out.print(StringEscapeUtils.escapeHtml(title));
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
	public void setId(String id)
	{
		this.id=id;
	}

	@Override
	public void release()
	{
		id=null;
		title=null;
		super.release();
	}


}
