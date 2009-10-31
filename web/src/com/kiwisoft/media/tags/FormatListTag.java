package com.kiwisoft.media.tags;

import com.kiwisoft.web.JspUtils;
import com.kiwisoft.format.FormatStringComparator;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Stefan Stiller
 * @since 02.10.2009
 */
public class FormatListTag extends StrutsBodyTagSupport
{
	private String value;
	private boolean sort;
	private String variant;
	private String separator;

	@SuppressWarnings({"unchecked"})
	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			Object rawValue=findValue(value);
			if (rawValue!=null)
			{
				String separator;
				if (this.separator==null) separator=", ";
				else separator=findString(this.separator);
				List list;
				if (rawValue instanceof List) list=(List) rawValue;
				else if (rawValue instanceof Collection) list=new ArrayList((Collection) rawValue);
				else if (rawValue instanceof Object[]) list=Arrays.asList(rawValue);
				else list=Collections.singletonList(rawValue);
				if (sort) Collections.sort(list, new FormatStringComparator());
				JspWriter out=pageContext.getOut();
				HttpServletRequest request=(HttpServletRequest) pageContext.getRequest();
				out.print(JspUtils.renderSet(request, list, variant, separator));
			}
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

	public boolean isSort()
	{
		return sort;
	}

	public void setSort(boolean sort)
	{
		this.sort=sort;
	}

	public String getVariant()
	{
		return variant;
	}

	public void setVariant(String variant)
	{
		this.variant=variant;
	}

	public String getSeparator()
	{
		return separator;
	}

	public void setSeparator(String separator)
	{
		this.separator=separator;
	}
}