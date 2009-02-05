package com.kiwisoft.xp.tags;

import java.util.Collection;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.xp.XPBean;

/**
 * @author Stefan Stiller
 */
public class NotEmptyTag extends TagSupport
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
		Object value=xpBean.getValue(this.value);
		if (value!=null)
		{
			if (value instanceof Collection)
			{
				if (((Collection)value).isEmpty()) return SKIP_BODY;
				return EVAL_BODY_INCLUDE;
			}
			else if (value instanceof String)
			{
				if (StringUtils.isEmpty((String)value)) return SKIP_BODY;
				return EVAL_BODY_INCLUDE;
			}
			else
				return EVAL_BODY_INCLUDE;
		}
		else
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
