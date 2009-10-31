package com.kiwisoft.media.show;

/**
 * @author Stefan Stiller
 * @since 06.10.2009
 */
public class ShowBooksAction extends ShowAction
{
	private ShowBooksTable table;

	@Override
	public String execute() throws Exception
	{
		super.execute();
		if (getShow()!=null) table=new ShowBooksTable(getShow());
		return SUCCESS;
	}

	public ShowBooksTable getTable()
	{
		return table;
	}
}