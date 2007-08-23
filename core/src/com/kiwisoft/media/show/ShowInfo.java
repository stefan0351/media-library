/*
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: Mar 28, 2003
 * Time: 10:10:18 PM
 */
package com.kiwisoft.media.show;

import com.kiwisoft.media.WebInfo;
import com.kiwisoft.persistence.DBDummy;

public class ShowInfo extends WebInfo
{
	public static final String SHOW="show";

	public ShowInfo(Show show)
	{
		setShow(show);
	}

	public ShowInfo(DBDummy dummy)
	{
		super(dummy);
	}

	public Show getShow()
	{
		return (Show)getReference(SHOW);
	}

	public void setShow(Show show)
	{
		setReference(SHOW, show);
	}

	public boolean isDefault()
	{
		return getShow().getDefaultInfo()==this;
	}
}
