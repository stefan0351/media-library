package com.kiwisoft.xp.tags;

import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;

import com.kiwisoft.xp.XPBean;

/**
 * @author Stefan Stiller
 */
public class IterateTag extends TagSupport
{
	private String bean;
	private String values;
	private String element;
	private Iterator iterator;

	@Override
	public int doStartTag() throws JspException
	{
		XPBean xpBean;
		if (bean!=null)
			xpBean=(XPBean)pageContext.getAttribute(bean);
		else
			xpBean=(XPBean)pageContext.getRequest().getAttribute("xp");
		Collection elements=xpBean.getValues(values);
		if (elements!=null)
		{
			iterator=elements.iterator();
			if (iterator.hasNext())
			{
				pageContext.setAttribute(this.element, iterator.next());
				return EVAL_BODY_INCLUDE;
			}
		}
		return SKIP_BODY;
	}

	@Override
	public int doAfterBody() throws JspException
	{
		if (iterator!=null && iterator.hasNext())
		{
			pageContext.setAttribute(this.element, iterator.next());
			return EVAL_BODY_AGAIN;
		}
		return SKIP_BODY;
	}

	public void setElement(String element)
	{
		this.element=element;
	}

	public void setBean(String bean)
	{
		this.bean=bean;
	}

	public void setCollection(String name)
	{
		values=name;
	}

	@Override
	public void release()
	{
		iterator=null;
		bean=null;
		values=null;
		element=null;
	}
}
