package com.kiwisoft.media.tags;

import com.kiwisoft.web.JspUtils;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * @author Stefan Stiller
 * @since 02.10.2009
 */
public class FormatTag extends StrutsBodyTagSupport
{
	private String value;
	private String variant;

	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			if (value==null) value="top";
			Object rawValue=findValue(value);
			JspWriter out=pageContext.getOut();
			HttpServletRequest request=(HttpServletRequest) pageContext.getRequest();
			out.print(JspUtils.render(request, rawValue, variant));
			return SKIP_BODY;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value=value;
	}

	public String getVariant()
	{
		return variant;
	}

	public void setVariant(String variant)
	{
		this.variant=variant;
	}
}

