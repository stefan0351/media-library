package com.kiwisoft.media.show;

/**
 * @author Stefan Stiller
 * @since 06.10.2009
 */
public class ShowTracksAction extends ShowAction
{
	private ShowTracksTable tracksTable;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		if (getShow()!=null) tracksTable=new ShowTracksTable(getShow());
		return SUCCESS;
	}

	public ShowTracksTable getTracksTable()
	{
		return tracksTable;
	}
}
