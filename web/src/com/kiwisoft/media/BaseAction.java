package com.kiwisoft.media;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Stefan Stiller
 * @since 01.10.2009
 */
public class BaseAction extends ActionSupport
{
	public String getPageTitle()
	{
		return "MediaLib";
	}
}
