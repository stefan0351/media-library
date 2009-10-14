package com.kiwisoft.media.show;

/**
 * @author Stefan Stiller
 * @since 06.10.2009
 */
public class ShowDetailsAction extends ShowAction
{
	private ShowInfo info;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		if (getShow()!=null)
		{
			info=getShow().getDefaultInfo();
			if (info!=null) return "info";
		}
		return SUCCESS;
	}

	public ShowInfo getInfo()
	{
		return info;
	}
}
