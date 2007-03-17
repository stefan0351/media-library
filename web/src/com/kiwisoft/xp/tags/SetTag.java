package com.kiwisoft.xp.tags;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;

import com.kiwisoft.xp.XPBean;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 19.06.2004
 * Time: 11:09:01
 * To change this template use File | Settings | File Templates.
 */
public class SetTag extends TagSupport
{
	private String bean;
	private String name;
	private String value;

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

	public void release()
	{
		name=null;
		bean=null;
		value=null;
	}
}
