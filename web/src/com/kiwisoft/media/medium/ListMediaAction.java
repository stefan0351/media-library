package com.kiwisoft.media.medium;

import com.kiwisoft.media.BaseAction;

/**
 * @author Stefan Stiller
 * @since 04.10.2009
 */
public class ListMediaAction extends BaseAction
{
	private Integer group;
	private MediaTable mediaTable;
	private int groupCount;

	@Override
	public String getPageTitle()
	{
		return "Media";
	}

	@Override
	public String execute() throws Exception
	{
		if (group==null) group=0;
		groupCount=MediumManager.getInstance().getGroupCount();
		mediaTable=new MediaTable(group);
		return super.execute();
	}

	public Integer getGroup()
	{
		return group;
	}

	public void setGroup(Integer group)
	{
		this.group=group;
	}

	public MediaTable getMediaTable()
	{
		return mediaTable;
	}

	public int getGroupCount()
	{
		return groupCount;
	}

	public String getGroupName(int group)
	{
		return Math.max(1, group*50)+"-"+(group*50+49);
	}
}
