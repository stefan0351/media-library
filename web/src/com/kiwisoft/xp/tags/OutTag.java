package com.kiwisoft.xp.tags;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;

import com.kiwisoft.xp.XPBean;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 */
public class OutTag extends TagSupport
{
	private String bean;
	private String value;

	public int doStartTag() throws JspException
	{
		XPBean xpBean;
		if (bean!=null)
			xpBean=(XPBean)pageContext.getAttribute(bean);
		else
			xpBean=(XPBean)pageContext.getRequest().getAttribute("xp");
		try
		{
			Collection values=xpBean.getValues(value);
			pageContext.getOut().print(StringUtils.formatAsEnumeration(values));
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	public void setValue(String value)
	{
		this.value=value;
	}

	public void setBean(String bean)
	{
		this.bean=bean;
	}

	public void release()
	{
		value=null;
		bean=null;
	}
}
