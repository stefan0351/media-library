package com.kiwisoft.xp.tags;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;

import com.kiwisoft.xp.XPBean;

/**
 * @author Stefan Stiller
 */
public class SetTag extends TagSupport
{
	private String bean;
	private String name;
	private String value;

	@Override
	public int doStartTag() throws JspException
	{
		XPBean xpBean;
		if (bean!=null)
			xpBean=(XPBean)pageContext.getAttribute(bean);
		else
			xpBean=(XPBean)pageContext.getRequest().getAttribute("xp");
		pageContext.setAttribute(name, xpBean.getValue(value));
		return SKIP_BODY;
	}

	public void setName(String name)
	{
		this.name=name;
	}

	public void setBean(String bean)
	{
		this.bean=bean;
	}

	public void setValue(String value)
	{
		this.value=value;
	}

	@Override
	public void release()
	{
		name=null;
		bean=null;
		value=null;
	}
}
