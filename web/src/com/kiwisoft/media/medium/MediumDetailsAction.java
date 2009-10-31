package com.kiwisoft.media.medium;

import com.kiwisoft.media.BaseAction;
import com.kiwisoft.web.RecentItemManager;
import com.kiwisoft.web.RecentIdObject;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class MediumDetailsAction extends BaseAction
{
	private Long mediumId;
	private Medium medium;
	private TracksTable tracksTable;

	@Override
	public String getPageTitle()
	{
		return "Media";
	}

	@Override
	public String execute() throws Exception
	{
		if (mediumId!=null) medium=MediumManager.getInstance().getMedium(mediumId);
		if (medium!=null)
		{
			RecentItemManager.getInstance().addItem(new RecentIdObject<Medium>(Medium.class, medium));
			tracksTable=new TracksTable(medium);
		}
		return super.execute();
	}

	public Long getMediumId()
	{
		return mediumId;
	}

	public void setMediumId(Long mediumId)
	{
		this.mediumId=mediumId;
	}

	public Medium getMedium()
	{
		return medium;
	}

	public TracksTable getTracksTable()
	{
		return tracksTable;
	}
}
