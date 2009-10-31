package com.kiwisoft.media.tags;

import com.kiwisoft.media.Navigation;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Stefan Stiller
 * @since 02.10.2009
 */
public class UrlTag extends StrutsBodyTagSupport
{
	private String icon;
	private String value;

	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			HttpServletRequest request=(HttpServletRequest) pageContext.getRequest();
			String url=null;
			if (icon!=null)
			{
				String iconName=findString(icon);
				url=request.getContextPath()+"/file/?type=Icon&name="+URLEncoder.encode(iconName, "UTF-8");
			}
			else
			{
				if (value==null) value="top";
				Object rawValue=findValue(value);
				if (rawValue!=null) url=Navigation.getLink(request, rawValue);
			}
			if (url!=null) pageContext.getOut().print(url);
			return SKIP_BODY;
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String icon)
	{
		this.icon=icon;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value=value;
	}
}